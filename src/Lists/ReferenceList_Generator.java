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
package Lists;

import Interfaces.Retrieve;
import MainObjects.ReferenceDoc;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

/**
 *
 * @author amaury
 */
public class ReferenceList_Generator<T extends ReferenceDoc> implements Retrieve<T> {

    private Constructor<T> ctorT;

    public ReferenceList_Generator(Constructor<T> ctor) throws IllegalAccessException, ClassNotFoundException, InstantiationException, IOException, SolrServerException {

        this.ctorT = ctor;
        

    }

    @Override
    public ArrayList<T> Retrieve_ReferenceList(SolrDocumentList SolrDocList) throws IOException, IllegalArgumentException,
            SolrServerException, InstantiationException, IllegalAccessException, InvocationTargetException {


        T Doc;
        Collection<java.lang.Object> keywords;
        String title;
        Integer book_id, product_id;
        boolean searchable;

        ArrayList<T> ReferenceList = new ArrayList<>(SolrDocList.size());

        for (SolrDocument CurrentSolrDoc : SolrDocList) {

            title = CurrentSolrDoc.getFieldValue("title").toString();
            book_id = Integer.parseInt(CurrentSolrDoc.getFieldValue("book_id").toString());
            product_id = Integer.parseInt(CurrentSolrDoc.getFieldValue("product_id").toString());
            searchable = Boolean.parseBoolean(CurrentSolrDoc.getFieldValue("searchable").toString());


            Doc = ctorT.newInstance(product_id, book_id, title, searchable);

            keywords = CurrentSolrDoc.getFieldValues("keywords");

            Doc.addKeywords(keywords);

            Doc.BalanceWeights();

            ReferenceList.add(Doc);
        }

        return ReferenceList;
    }
}
