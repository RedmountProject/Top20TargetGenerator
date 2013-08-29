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

import Logging.FileLogger;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author amaury
 *
 * NEVER EDIT THIS OBJECT : EXTENDS YOUR OWN OBJECT FROM THIS ONE INSTEAD
 */
public abstract class ReferenceTopDoc implements Comparable<ReferenceTopDoc> {

    public Integer book_id;
    public Integer product_id;
    public String title;
    public double sum = new Double(0);
    public Integer MatchedKeywordsNumber = 0;
    public ArrayList<Double> Weights;
    public ArrayList<TopKeyword> TopKeywords = new ArrayList<>();
    private ArrayList<Keyword> ReferenceKeyword;

    public ReferenceTopDoc(Integer product_id, Integer book_id, String title) {
        
        this.Weights = new ArrayList();
        this.product_id = product_id;
        this.book_id = book_id;
        this.title = title;
    }

    private void addWeight(double Weight) {
        MatchedKeywordsNumber++;
        Weights.add(Weight);
    }

    public void AddKeyword(Keyword matched_kw, ArrayList<Keyword> ReferenceKeyword, Double final_calculated_weight, Double UnbalancedReferenceWeight, Double BalandReferenceWeight) {
        addWeight(final_calculated_weight);

        this.ReferenceKeyword = (ArrayList<Keyword>) ReferenceKeyword.clone();

        TopKeyword top_kw = new TopKeyword();

        top_kw.setKeyword(matched_kw.keyword);
        top_kw.setLemma(matched_kw.lemma);
        top_kw.setCurrent_weight(matched_kw.weight);

        top_kw.setFinal_calculated_weight(final_calculated_weight);
        top_kw.setUnbalanced_weight(UnbalancedReferenceWeight);
        top_kw.setBalanced_weight(BalandReferenceWeight);
        top_kw.setKeyword_lemma(matched_kw.getKeywordLemma());

        TopKeywords.add(top_kw);

    }

    private final String FORMAT_STRING = "%s\t|\t%2$,.3f\t|\t%3$,.3f\t|\t%4$,.3f\t|\t%5$,.3f";
    public void Log(FileLogger fl, int  i) {
        
        String file_title = i+"-"+book_id + "-" + title.replaceAll(" ", "_")+ ".log";
        
        fl.getLogger(file_title);
        
        fl.info("\ntop book product_id : " + product_id + "book_id : "+book_id+" title : " + title + ", number of keywords matched : " + MatchedKeywordsNumber + " sum : " + sum + "\n");

        for (Iterator<Keyword> it = ReferenceKeyword.iterator(); it.hasNext();) {
            Keyword keyword = it.next();
            fl.info("Current TopDoc Keywords : " + keyword.getKeywordLemma()+"  "+keyword.weight);
        }
        fl.info("keyword\t|\tpoid du mot clé\t|\tpoids du mot clé dans le livre de référence\t|\tValeur pondérée à l'insertion du mot clé ( cf: classe hérité de ReferenceDoc )\t|\t Valeur de Comparaison finale");
        for (Iterator<TopKeyword> it = TopKeywords.iterator(); it.hasNext();) {
            TopKeyword topKeyword = it.next();
            fl.info(String.format(FORMAT_STRING,topKeyword.keyword_lemma, topKeyword.current_weight, topKeyword.unbalanced_weight, topKeyword.balanced_weight,  topKeyword.final_calculated_weight));
            //fl.info("final calc value : " + topKeyword.final_calculated_weight + " Balanced weight : " + topKeyword.balanced_weight + " unbalanced weight : " + topKeyword.unbalanced_weight);
        }
    }
    
    public void Afficher() {

        
        
        System.out.println("\ntop book product_id : " + product_id + " title : " + title + ", number of keywords matched : " + MatchedKeywordsNumber + " sum : " + sum + "\n");

        for (Iterator<Keyword> it = ReferenceKeyword.iterator(); it.hasNext();) {
            Keyword keyword = it.next();
            System.out.println("Current TopDoc Keywords : " + keyword.getKeywordLemma());
        }
        for (Iterator<TopKeyword> it = TopKeywords.iterator(); it.hasNext();) {
            TopKeyword topKeyword = it.next();
            System.out.println("------------------------------------------ " + topKeyword.keyword_lemma + " --------------------------------------------- " + topKeyword.current_weight);
            System.out.println("final calc value : " + topKeyword.final_calculated_weight + " Balanced weight : " + topKeyword.balanced_weight + " unbalanced weight : " + topKeyword.unbalanced_weight);
        }
    }

    public abstract void AddUp();

    
}
