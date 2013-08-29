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
public class TopKeyword implements Comparable<TopKeyword> {
    
    public String keyword;
    
    public double final_calculated_weight;
    public double current_weight;
    public double balanced_weight;
    public double unbalanced_weight;
    
    public String lemma;

    public String keyword_lemma;

    public void setKeyword_lemma(String keyword_lemma) {
        this.keyword_lemma = keyword_lemma;
    }

    
    public double getBalanced_weight() {
        return balanced_weight;
    }

    public void setBalanced_weight(double balanced_weight) {
        this.balanced_weight = balanced_weight;
    }

    public double getCurrent_weight() {
        return current_weight;
    }

    public void setCurrent_weight(double current_weight) {
        this.current_weight = current_weight;
    }

    public double getFinal_calculated_weight() {
        return final_calculated_weight;
    }

    public void setFinal_calculated_weight(double final_calculated_weight) {
        this.final_calculated_weight = final_calculated_weight;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public double getUnbalanced_weight() {
        return unbalanced_weight;
    }

    public void setUnbalanced_weight(double unbalanced_weight) {
        this.unbalanced_weight = unbalanced_weight;
    }

    @Override
    public int compareTo(TopKeyword o) {
        return Double.compare(o.final_calculated_weight, final_calculated_weight);
    }

   
}
