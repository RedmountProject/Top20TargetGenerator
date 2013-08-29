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

import MainObjects.Keyword;
import MainObjects.ReferenceDoc;
import MainObjects.ReferenceTopDoc;
import java.util.*;

/**

 * @author amaury
 */
public class FullyBalanced<U extends ReferenceTopDoc> extends ReferenceDoc {

    private Comparator<ReferenceTopDoc> SumOrder = new Comparator<ReferenceTopDoc>() {

        public int compare(ReferenceTopDoc t1, ReferenceTopDoc t2) {
            Double v = t1.sum - t2.sum;
            if (v < 0) {
                return 1;
            }
            if (v > 0) {
                return -1;
            }
            return 0;
        }
    };

    public FullyBalanced(Integer product_id, Integer book_id, String title, Boolean pSearchable) {
        super(product_id, book_id, title, pSearchable);
    }

    @Override
    public Double calc(double CurrentWeight, double ReferenceWeight) {

        return CurrentWeight * ReferenceWeight;

    }

    @Override
    public void BalanceWeights() {
        Double coef;
        Double TempSum;

        if (!Keywords.isEmpty()) {

            TempSum = sum();

            Iterator it = Keywords.iterator();

            while (it.hasNext()) {
                Keyword kw = (Keyword) it.next();


                Double weight = kw.weight;

                coef = (((weight) * 100.f) / TempSum);

                kw.setBalancedWeight(weight);
            }
        }

    }

    private Double sum() {

        Iterator it = Keywords.iterator();
        Double TempSum = new Double(0);
        Double Weight;

        while (it.hasNext()) {
            Keyword kw = (Keyword) it.next();
            Weight = kw.weight;

            TempSum += Weight;
        }
        return TempSum;
    }

    @Override
    public void sort() {
        for (Iterator it = TopList.iterator(); it.hasNext();) {
            U doc = (U) it.next();
            doc.AddUp();
        }

        Collections.sort(TopList);

        cutTopList();

    }

    
    private void cutTopList() {

        ArrayList<U> TempTopList = (ArrayList<U>) TopList.clone();
        TopList.clear();

        ArrayList<U> List = new ArrayList<>();

        int TempMatchNumber = ((U) TempTopList.get(0)).MatchedKeywordsNumber;

        for (Iterator it = TempTopList.iterator(); it.hasNext();) {
            U doc = (U) it.next();

            if (doc.MatchedKeywordsNumber == TempMatchNumber) {
                List.add(doc);
            } else {
                List = sortList(List);
                TopList.addAll(List);
                List.clear();
                TempMatchNumber = doc.MatchedKeywordsNumber;
            }
        }
        TopList.addAll(List);
    }

    private ArrayList<U> sortList(ArrayList<U> List) {

        Collections.sort(List, SumOrder);

        return List;
    }
}
