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
package Objects;

import MainObjects.ReferenceTopDoc;
import java.util.Comparator;
import java.util.Iterator;

/**
 *
 * @author amaury
 */
public class TopDocMatchSorted extends ReferenceTopDoc  {

    
            
    public TopDocMatchSorted(Integer product_id, Integer book_id, String title) {
        super(product_id, book_id, title);
    }

    @Override
    public void AddUp() {
        double p;
        sum = new Double(0);
        for (Iterator<Double> it = Weights.iterator(); it.hasNext();) {
            p = it.next();
            sum += p;

        }
    }
    
    @Override
    public int compareTo(ReferenceTopDoc t) {

        return (t.MatchedKeywordsNumber - MatchedKeywordsNumber);
            
    }
    
    
}
