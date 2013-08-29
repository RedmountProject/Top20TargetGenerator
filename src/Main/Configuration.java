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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author amaury
 */
public class Configuration extends Properties {

    public static final Logger LOG = LoggerFactory.getLogger(Configuration.class.getName());

    public Configuration() {
        super();
        try {
            FileInputStream in = new FileInputStream("conf/sanspapier-default.xml");
            load(in);
        } catch (IOException ex) {
            LOG.error("Error parsing Config file ", ex);
        }
    }
    
    public int getInt(String key){
        return Integer.parseInt(getProperty(key));
    }
    
    public String getString(String key){
        return getProperty(key);
    }
    public Boolean getBoolean(String key){
        return Boolean.parseBoolean(getProperty(key));
    }
}
