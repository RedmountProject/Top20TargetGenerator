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
package MainObjects;

/**
 *
 * @author amaury
 */
public class Keyword {

    public final String keyword;
    public final double weight;
    public String lemma;
    public double balanced_weight;

    public Keyword(String pKeyword, String pWeight, String pLemma) {
        this.keyword = pKeyword;
        this.weight = Double.parseDouble(pWeight);
        this.lemma = pLemma;
    }

    Keyword(String pKeyword, String pWeight) {
        this.keyword = pKeyword;
        this.weight = Double.parseDouble(pWeight);
    }

    public String toString(){
       return getKeywordLemma()+" "+weight;
    }
    
    public void setBalancedWeight(double pWeight) {
        this.balanced_weight = pWeight;
    }
    
    public String getKeywordLemma(){
        return keyword+"."+lemma;
    }
}
