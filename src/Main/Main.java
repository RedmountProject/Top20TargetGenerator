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

import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author amaury
 *
 */
public class Main {

    public static final Logger LOG = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SQLException {

        Integer l_int = Integer.parseInt(args[0]);
        Integer h_int = Integer.parseInt(args[1]);
        
        Configuration conf = new Configuration();

        String algoString = "Objects." + conf.getProperty("AlgoMethod");

        Class<?> algoClass = Class.forName(algoString);

        LOG.info( "Using "+algoString+" as Calculating method ");

        String topString = "Objects." + conf.getProperty("TopStorage");

        LOG.info( "Using "+topString+" as Storage & Sorting method ");

        Class<?> topClass = Class.forName(topString);

        Class[] CtorAlgoArgs = new Class[]{Integer.class, Integer.class, String.class, Boolean.class};
        Class[] CtorStorageArgs = new Class[]{Integer.class, Integer.class, String.class};

        Generator generator = new Generator(algoClass.getConstructor(CtorAlgoArgs), topClass.getConstructor(CtorStorageArgs));
        int ret = generator.Generate(conf, l_int, h_int);

        System.exit(ret);

    }
}
