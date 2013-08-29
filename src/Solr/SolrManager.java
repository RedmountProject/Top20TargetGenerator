/*  Copyright (C) 2013 BRISOU Amaury

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation; either version 2 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License along
    with this program; if not, write to the Free Software Foundation, Inc.,
    51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package Solr;

import Main.Configuration;
import MainObjects.ReferenceTopDoc;
import com.sun.org.apache.xerces.internal.util.URI.MalformedURIException;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author amaury
 */
public final class SolrManager {

    public static final Logger LOG = LoggerFactory.getLogger(SolrManager.class.getName());
    public static ArrayList<SolrInputDocument> InputDocList = new ArrayList<>();
    private SolrInputDocument InputDoc = null;
    private final Configuration conf;

    public SolrManager(Configuration conf) throws IOException, MalformedURIException, SolrServerException {

        this.conf = conf;


    }

    public SolrDocumentList getSolrDocs() throws SolrServerException, IOException {
        LOG.info("Fetching SolDocs");

        int MAX_FETCH_ROWS = conf.getInt("solr.fetch.size");

        String SolrCatalogUrl = conf.getString("solr.url");
        CommonsHttpSolrServer solrCatalog = new CommonsHttpSolrServer(SolrCatalogUrl);

        SolrDocumentList docsCatalog;

        SolrQuery solrQuery = new SolrQuery().setQuery(/*
                 * " (searchable:false AND
                 */"keywords:[ 1 TO *] "/*
                 * )" *OR ( searchable:true) "
                 */).setRows(MAX_FETCH_ROWS).addSortField("book_id", SolrQuery.ORDER.asc);


        QueryResponse rsp = solrCatalog.query(solrQuery);

        docsCatalog = rsp.getResults();

//        SolrInputDocument InputDoc;
//        for (SolrDocument CurrentSolrDoc : docsCatalog) {
//            CurrentSolrDoc.removeFields("searchable");
//            CurrentSolrDoc.addField("searchable", false);
//            InputDoc = ClientUtils.toSolrInputDocument(CurrentSolrDoc);
//            solrCatalog.add(InputDoc);
//        }
//        solrCatalog.commit();


        LOG.info("Doc Number : "+docsCatalog.getNumFound());

        return docsCatalog;
    }

    public void insert(ArrayList<? extends ReferenceTopDoc> TopBookList, SolrDocument CurrentSolrDoc) throws IOException, SolrServerException, IllegalAccessException, ClassNotFoundException, InstantiationException {

        int COMMIT_SIZE = conf.getInt("solr.commit.size");
        boolean daily = conf.getBoolean("daily");

        String SolrCatalogUrl = conf.getString("solr.url");
        CommonsHttpSolrServer solrCatalog = new CommonsHttpSolrServer(SolrCatalogUrl);

        int InputListSize;

        int i = 0;

        CurrentSolrDoc.removeFields("top20");

        for (ReferenceTopDoc TopBook : TopBookList) {
            CurrentSolrDoc.addField("top20", TopBook.product_id);
            i++;
            if (i > 100) {
                break;
            }
        }

        if (conf.getBoolean("change.status")) {
            CurrentSolrDoc.removeFields("searchable");

            CurrentSolrDoc.addField("searchable", true);

            CurrentSolrDoc.removeFields("author_searchable");
            CurrentSolrDoc.addField("author_searchable", true);
        }

        InputDoc = ClientUtils.toSolrInputDocument(CurrentSolrDoc);

        InputDocList.add(InputDoc);
        InputListSize = InputDocList.size();
        if (InputListSize >= COMMIT_SIZE) {
            solrCatalog.add(InputDocList);
            solrCatalog.commit();
            LOG.info("Queue Committed to Solr");
            InputDocList.clear();
        }

        LOG.info("Book Added To solr Queue "+InputListSize+"/"+COMMIT_SIZE);
    }

    public void MyCommit() throws SolrServerException, IOException {
        if (InputDocList.size() > 0) {
            System.out.println("InputDocList.size() : " + InputDocList.size());

            String SolrCatalogUrl = conf.getString("solr.url");
            CommonsHttpSolrServer solrCatalog = new CommonsHttpSolrServer(SolrCatalogUrl);

            solrCatalog.add(InputDocList);
            solrCatalog.commit();
        }
    }
}
