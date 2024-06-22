package com.ejt.util;

import java.util.Comparator;
import java.util.Map;

public class PrecedenceComp implements Comparator<Integer> {

    private Map<Integer, Double> groupItems = null;
    private boolean ascending = true;

    public PrecedenceComp(Map<Integer, Double> groupItems, boolean ascending) {
        this.groupItems = groupItems;
        this.ascending = ascending;
    }

    public int compare(Integer int1, Integer int2) {
        if (int1 == null || int1 == null || groupItems == null) {
            return -1;
        }
        Double doub1 = groupItems.get(int1);
        Double doub2 = groupItems.get(int2);
        if (doub1 == null || doub2 == null) {
            return -1;
        } else if (Math.abs(doub1.doubleValue() - doub2.doubleValue()) < Precedence.tolerance) {
            return 0;
        } else if (doub1.doubleValue() > doub2.doubleValue()) {
            if (ascending) {
                return 1;
            } else {
                return -1;
            }
        } else {
            if (ascending) {
                return -1;
            } else {
                return 1;
            }
        }
    }
}
