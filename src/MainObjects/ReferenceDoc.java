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

import Interfaces.Balance;
import Logging.FileLogger;
import Objects.Balanced;
import Utils.FileManagement;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;


/**
 *
 * @author amaury
 *
 * NEVER EDIT THIS OBJECT : EXTENDS YOUR OWN OBJECT FROM THIS ONE INSTEAD
 */
public abstract class ReferenceDoc<T extends ReferenceDoc, U extends ReferenceTopDoc> implements Comparable<ReferenceDoc>, Balance {

    public ArrayList<Keyword> Keywords = new ArrayList<>();
    public ArrayList<U> TopList = new ArrayList<>();
    protected double sum = new Double(0);
    public Integer book_id;
    public Integer product_id;
    public String title;
    public Constructor<U> ctorU;
    public final boolean searchable;

    public ReferenceDoc(int product_id, int book_id, String title, boolean pSearchable) {
        this.product_id = product_id;
        this.book_id = book_id;
        this.title = title;
        this.searchable = pSearchable;
    }

    public boolean hasBookIdinOwnTop(int book_id) {
        boolean found = false;
        for (U doc : TopList) {
            if (doc.book_id == book_id) {
                found = true;
            }
        }
        return found;
    }

    public void addKeywords(Collection<? extends Object> pKeywords) {

        String CurrentWeight, CurrentKeyword, CurrentKeywordLemma, CurrentLemma;
        for (Object keyword : pKeywords){

            CurrentKeywordLemma = ((keyword.toString()).split("\\|"))[0];

            CurrentKeyword = CurrentKeywordLemma.split("\\.")[0];
            if (CurrentKeywordLemma.split("\\.").length < 2) {
                CurrentLemma = "NP";
            } else {
                CurrentLemma = CurrentKeywordLemma.split("\\.")[1];
            }
            CurrentWeight = keyword.toString().split("\\|")[1];

            Keywords.add(new Keyword(CurrentKeyword, CurrentWeight, CurrentLemma));
        }
    }

    private Double getUnBalancedWeight(ArrayList<Keyword> pKeywords, String CurrentKeywordLemma) {

        for (Iterator it = pKeywords.iterator(); it.hasNext();) {
            Keyword kw = (Keyword) it.next();
            if (kw.getKeywordLemma().equals(CurrentKeywordLemma)) {
                return kw.weight;
            }
        }
        return null;
    }

    private Double getBalancedWeight(ArrayList<Keyword> pKeywords, String CurrentKeywordLemma) {

        for (Iterator it = pKeywords.iterator(); it.hasNext();) {
            Keyword kw = (Keyword) it.next();
            if (kw.getKeywordLemma().equals(CurrentKeywordLemma)) {
                return kw.balanced_weight;
            }
        }
        return null;
    }

    protected void commit(T ReferenceDoc, U TopDoc) {
        ReferenceDoc.TopList.add(TopDoc);
    }

    public U newTopDoc() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        U TopDoc = (U) ctorU.newInstance(product_id, book_id, title);
        return TopDoc;
    }

    @Override
    public int compareTo(ReferenceDoc t) {
        return Double.compare(t.sum, sum);
    }

    protected void AddMatch(U TopDoc, Keyword matched_kw, ArrayList<Keyword> ReferenceKeywords, Double UnBalancedReferenceWeight, Double BalancedReferenceWeight) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Double final_calculated_weight;
        final_calculated_weight = calc(matched_kw.balanced_weight, BalancedReferenceWeight);
        TopDoc.AddKeyword(matched_kw, ReferenceKeywords, final_calculated_weight, UnBalancedReferenceWeight, BalancedReferenceWeight);
    }

    public ReferenceDoc DecideAndAdd(T ReferenceDoc, Constructor<U> ctorU) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        this.ctorU = ctorU;
        ArrayList<Keyword> LocalReferenceKeywords = ReferenceDoc.Keywords;
        Double UnBalancedReferenceWeight, BalancedReferenceWeight;
        U TopDoc = (U) newTopDoc();

        Iterator itKws = Keywords.iterator();
        while (itKws.hasNext()) {
            Keyword kw = (Keyword) itKws.next();
            if (hasSameKeywordLemma(LocalReferenceKeywords, kw.getKeywordLemma())) {
                BalancedReferenceWeight = getBalancedWeight(ReferenceDoc.Keywords, kw.getKeywordLemma());
                UnBalancedReferenceWeight = getUnBalancedWeight(ReferenceDoc.Keywords, kw.getKeywordLemma());
                AddMatch(TopDoc, kw, Keywords, UnBalancedReferenceWeight, BalancedReferenceWeight);
            }
        }

        if (TopDoc.MatchedKeywordsNumber > 0) {
            commit(ReferenceDoc, TopDoc);
        }
        return ReferenceDoc;
    }

    public void sleep(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException ex) {
            
        }
    }

    public void Log(FileLogger fl) {

        String file_and_directory_title = book_id + "-" + title.replaceAll(" ", "_");
        boolean mkdir_ret = FileManagement.mkdir(fl.getPath(), file_and_directory_title);

        if(!mkdir_ret){
            return;
        }
        fl.appendLogger(file_and_directory_title);
        
        fl.getLogger(file_and_directory_title + ".log");


        fl.info("################################# " + product_id + " #### "+book_id+" #### " + title + " #####################################");
        
        for (Iterator<Keyword> it = Keywords.iterator(); it.hasNext();) {
            Keyword keyword = it.next();
            fl.info(keyword.toString());
        }
        fl.info("#############################################################################################################");

        FileManagement.mkdir(fl.getPath(), "top20");
        fl.appendLogger("top20");
        
        int i = 0;
        for (Iterator it = TopList.iterator(); it.hasNext() && i < 100;) {
            U doc = (U) it.next();
            doc.Log(fl, i++);
        }


    }

    public void afficher() {

        System.out.println("#################################" + product_id + "########" + title + "#####################################");
        int i = 0;

        for (Iterator it = TopList.iterator(); it.hasNext();) {
            i++;
            U doc = (U) it.next();
            doc.Afficher();
        }
        System.out.println("################################# Book Count in top : " + i + " #############################################");

    }

    private boolean hasSameKeywordLemma(ArrayList<Keyword> LocalReferenceKeywords, String keywordLemma) {
        for (Iterator<Keyword> it = LocalReferenceKeywords.iterator(); it.hasNext();) {
            Keyword keyword = it.next();
            if (keyword.getKeywordLemma().equals(keywordLemma)) {
                return true;
            }
        }
        return false;
    }
}
