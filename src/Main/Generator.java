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
package Main;

import Database.DatabaseManager;
import Exception.ExceptionHandler;
import Lists.ReferenceList_Generator;
import Logging.FileLogger;
import MainObjects.ReferenceDoc;
import MainObjects.ReferenceTopDoc;
import Solr.SolrManager;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author amaury
 */
public class Generator<T extends ReferenceDoc, U extends ReferenceTopDoc> {

    public static FileLogger fl;
    public static final Logger LOG = LoggerFactory.getLogger(Generator.class.getName());
    private final Constructor<T> ctorT;
    private final Constructor<U> ctorU;
    private final int SPEC_ID = 3352;

    public Generator(Constructor<T> ctorT, Constructor<U> ctorU) {
        this.ctorT = ctorT;
        this.ctorU = ctorU;
    }

    /**
     * Retrieve_ReferenceList Call Method BalanceWeights() from the <T> Object
     * <T> must be extended from ReferenceDoc and implements his interfaces
     * since ReferencDoc is abstract
     *
     * @param SolrDocumentList Object fetched from an Apache Solr Server
     * @return ArrayList of Object type <T>. Keywords Added onto each docs have
     * been Balanced according to the <T> BalanceWeights() method
     * @throws IOException, IllegalArgumentException, SolrServerException,
     * InstantiationException, IllegalAccessException,
     * InvocationTargetException, ClassNotFoundException
     */
    public ArrayList<T> getReferenceList(SolrDocumentList docsCatalog) throws IOException, IllegalArgumentException, SolrServerException, InstantiationException, IllegalAccessException, InvocationTargetException, ClassNotFoundException {
        ReferenceList_Generator<T> ReferenceListGenerator;
        ReferenceListGenerator = new ReferenceList_Generator(ctorT);
        ArrayList<T> ReferenceList;

        ReferenceList = ReferenceListGenerator.Retrieve_ReferenceList(docsCatalog);
        return ReferenceList;
    }

    public int Generate(Configuration conf, Integer l_int, Integer h_int) throws SQLException {
        try {
            //fl = new FileLogger(conf.getProperty("log_path"));
            SolrManager solr = new SolrManager(conf);
            DatabaseManager mysql = new DatabaseManager(conf);
            SolrDocumentList docsCatalog = solr.getSolrDocs();
            ArrayList<T> ReferenceList = getReferenceList(docsCatalog);

            T CurrentReferenceDoc;
            boolean found, job = conf.getBoolean("job");
            int i = 0, len = ReferenceList.size(), l_count = 0;

            LOG.info("Begginning Generation between " + l_int + " and " + h_int);
            for (int j = l_int; j < h_int && j < len; j++) {
                CurrentReferenceDoc = ReferenceList.get(j);
                
                if(CurrentReferenceDoc.searchable) continue;
                
                log(h_int - l_int);

                //fl.reset();
                l_count++;
                LOG.info("Book : " + l_count + "/" + (h_int - l_int) + ", Book_id : " + CurrentReferenceDoc.book_id);
                //  if (CurrentReferenceDoc.product_id == SPEC_ID) {
                for (T Doc : ReferenceList) {
                    found = CurrentReferenceDoc.hasBookIdinOwnTop(Doc.book_id);
                    if (!found && !CurrentReferenceDoc.book_id.equals(Doc.book_id)) {
                        CurrentReferenceDoc = (T) Doc.DecideAndAdd(CurrentReferenceDoc, ctorU);
                    }
                }

                CurrentReferenceDoc.sort();

//                    job = Boolean.parseBoolean(conf.getProperty("job"));
//                    if (job) {
//                        if (CurrentReferenceDoc.product_id == SPEC_ID) {
//                            CurrentReferenceDoc.Log(fl);
//                            //CurrentReferenceDoc.afficher();
//                            //System.exit(1);
//                        }
//                    }

                if (job) {
                    int CurrentBookId = CurrentReferenceDoc.book_id;
                    for (SolrDocument CurrentSolrDoc : docsCatalog) {
                        int CurrentSolrDocBookId = Integer.parseInt(CurrentSolrDoc.getFieldValue("book_id").toString());
                        if (CurrentSolrDocBookId == CurrentBookId) {
//                                if (i == 0) {
//                                    CurrentReferenceDoc.Log(fl);
//                                    i = 1;
//                                }
                            int CurrentSolrDocProductId = Integer.parseInt(CurrentSolrDoc.getFieldValue("product_id").toString());
                            solr.insert(CurrentReferenceDoc.TopList, CurrentSolrDoc);
                             if(conf.getBoolean("change.status")){
                                mysql.updateStatus(CurrentSolrDocProductId);
                             }
                        }
                    }
                    //i = 0;
                }
                CurrentReferenceDoc.TopList.clear();
                i++;
                // }
            }
            if (conf.getBoolean("job")) {
                solr.MyCommit();
                mysql.commitStatus();
                LOG.info("Queue Committed to Solr");
            }
        } catch (SolrServerException | ClassNotFoundException | IOException | IllegalArgumentException | InvocationTargetException | InstantiationException | IllegalAccessException ex) {
            new ExceptionHandler(ex);
        }
        return 0;
    }
    private long last_time = new Date().getTime() / 1000;
    private int book_count = 0;
    private long sum = 0;

    private void log(int nb_books) {
        book_count++;
        long time = new Date().getTime() / 1000;
        long average_time = time - last_time;
        sum += average_time;
        
        LOG.info("\nBook Count : "+book_count+"\n"
                + "Execution Time : " + average_time + "\n"
                + "Average Execution Time : " + sum / book_count + "\n"
                + "Elapsed Time : "+getHours(book_count *  average_time)+"\n"
                + "Estimated Remaining Time : "+ getHours(((sum/ book_count) * nb_books) - (book_count *  average_time) )+"\n"
                + "Estimated Total Execution Time : " + (sum/ book_count) * nb_books + " seconds\n"
                + "Estimated Total Execution Time : " + getHours(sum / book_count * nb_books ));
        last_time = time;
    }

    private static String getHours(Long d) {
        long hours, minutes;

        hours = d / 3600;
        minutes =  (d - (hours * 3600)) / 60 ;

        return Math.round(hours)+ " hours and " + minutes + " minutes";

    }
}
