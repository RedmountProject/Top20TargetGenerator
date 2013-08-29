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
package Database;

import Main.Configuration;
import MainObjects.ReferenceTopDoc;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager extends Properties {

    public static final Logger LOG = LoggerFactory.getLogger(DatabaseManager.class.getName());
    private Configuration conf;
    private static LinkedList<String> requetes = new LinkedList<>();
    private static int CommitQueueLength = 0;
    private java.sql.Connection CatalogCon;
    private final int COMMIT_SIZE;

    public DatabaseManager(Configuration conf) throws IllegalAccessException, ClassNotFoundException, InstantiationException {
        this.conf = conf;
        String COMMIT_SIZE_TEMP = conf.getString("db.commit.size");
        COMMIT_SIZE = Integer.parseInt(COMMIT_SIZE_TEMP);

        connect();
    }

    private void connect() throws ClassNotFoundException, InstantiationException, IllegalAccessException {

        String DatabaseAddress = conf.getString("db.address");
        String DatabasePort = conf.getString("db.port");
        String DatabaseName = conf.getString("db.name");
        String DatabaseUserName = conf.getString("db.username");
        String DatabasePassword = conf.getString("db.password");

        CatalogCon = Jdbc.Connection(DatabaseAddress, DatabasePort, DatabaseName, DatabaseUserName, DatabasePassword, false);

    }

    public void insert(ArrayList<ReferenceTopDoc> TopBookList, int CurrentBookId) throws SQLException {
        delete(CurrentBookId);
        insertRequest(TopBookList, CurrentBookId);

        if (CommitQueueLength >= COMMIT_SIZE) {
            commit();
            LOG.info("Queue Committed to Database");
        }
    }

    private void delete(int CurrentBookId) throws SQLException {

        String requete = "DELETE FROM `catalog`.`top` WHERE `book_id_fk` = '" + CurrentBookId + "';";
        try (PreparedStatement ContentStatement = CatalogCon.prepareStatement(requete)) {
            ContentStatement.execute();
            CatalogCon.commit();

        } catch (SQLIntegrityConstraintViolationException e) {
            if (CatalogCon != null) {
                try {
                    CatalogCon.rollback();

                } catch (Exception excep) {
                    LOG.error("Couldn't delete old top20 into database");

                }
            }
        }
    }

    private void insertRequest(ArrayList<ReferenceTopDoc> TopBookList, int CurrentBookId) {
        int i = 0;
        for (ReferenceTopDoc TopBook : TopBookList) {
            i++;
            if (i > 100) {
                break;
            }

            requetes.push("INSERT INTO `catalog`.`top` ( `book_id_fk`, `topN_book_id_fk` ) VALUES (  '" + CurrentBookId + "' , '" + TopBook.product_id + "');");
            CommitQueueLength++;
        }
    }

    public void commit() throws SQLException, SQLException {
        try {
            while (!requetes.isEmpty()) {
                String requete = requetes.getFirst();
                requetes.pop();
                PreparedStatement UrlStatement = CatalogCon.prepareStatement(requete);
                UrlStatement.execute();
            }
            CatalogCon.commit();
        } catch (SQLIntegrityConstraintViolationException e) {
            if (CatalogCon != null) {
                try {
                    CatalogCon.rollback();
                } catch (Exception excep) {
                    LOG.error("Error During Top20 Insertion");
                }
            }
        }
    }

    public void updateStatus(int CurrentSolrDocProductId) throws SQLException {
        try {
            String requete = "UPDATE product SET searchable = 1 WHERE product_id = " + CurrentSolrDocProductId;
            PreparedStatement UrlStatement = CatalogCon.prepareStatement(requete);
            UrlStatement.execute();
            CommitQueueLength++;
            LOG.info("Book queued for searchable field update " + CommitQueueLength + "/" + COMMIT_SIZE + " , product_id : " + CurrentSolrDocProductId);
            if (CommitQueueLength >= COMMIT_SIZE) {
                commit();
                CommitQueueLength = 0;
                LOG.info("Queue Committed to Database");
            }
        } catch (SQLIntegrityConstraintViolationException e) {
            if (CatalogCon != null) {
                try {
                    CatalogCon.rollback();
                } catch (Exception excep) {
                    LOG.info("Error updating searchable field for product_id : " + CurrentSolrDocProductId);
                }
            }
        }
    }

    public void commitStatus() throws SQLException {
        commit();
    }

    /**
     * Useless by now
     * @return 
     */
    public ArrayList<Integer> getModifiedProducts() {
        LOG.info("Fetcheding new Products or Modified");
        try {
            String requete = "SELECT DISTINCT book_id_fk FROM product WHERE update_date > DATE_SUB(NOW(), INTERVAL '1 6' DAY_HOUR )";
            PreparedStatement UrlStatement = CatalogCon.prepareStatement(requete);
            ResultSet rs = UrlStatement.executeQuery();
            CommitQueueLength++;
            ArrayList<Integer> new_book_ids = new ArrayList<>(rs.getFetchSize());
            while (rs.next()) {
                new_book_ids.add(rs.getInt("book_id_fk"));
            }
            LOG.info("Fetched " + new_book_ids.size() + " new Products or Modified");
            return new_book_ids;
        } catch (SQLException ex) {
            LOG.error("Error Fetching new Products");
        }
        return null;
    }
}
