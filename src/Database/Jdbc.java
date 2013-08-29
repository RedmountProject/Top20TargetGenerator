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

import java.sql.DriverManager;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Jdbc {

    public static final Logger LOG = LoggerFactory.getLogger(Jdbc.class.getName());
    
    public enum AUTO_COMMIT {

        FALSE, TRUE;
    }


    public static java.sql.Connection Connection(String DatabaseAddress, String DatabasePort, String DatabaseName,
            String DatabaseUserName, String DatabasePassword, final boolean AUTO_COMMIT) throws ClassNotFoundException, InstantiationException, IllegalAccessException {


        String DatabaseUrl = "jdbc:mysql://" + DatabaseAddress + ":" + DatabasePort + "/" + DatabaseName + "?user=" + DatabaseUserName
                + "&password=" + DatabasePassword;

        LOG.info("Connecting to database : "+DatabaseUrl);
        
        java.sql.Connection connect = null;
        try {

            Class.forName("com.mysql.jdbc.Driver").newInstance(); //JdbcOdbcDriver
            connect = DriverManager.getConnection(DatabaseUrl);
            connect.setAutoCommit(AUTO_COMMIT);
            LOG.info("Connection Established");

        } catch (SQLException e) {
             LOG.error("Connecting To MySql : "+DatabaseUrl+ "  "+e );
             System.exit(1);
        }
        return connect;
        
    }
}
