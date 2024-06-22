package com.ejt.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrecedenceGroup {

    private List<Integer> precList = null, highestPrecList = null;
    private Map<Integer, Double> items = null;
    private int id = -1;
    private boolean ascending = true;

    public PrecedenceGroup() {
        items = new HashMap<Integer, Double>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public void add(int itemId, double precVal) {
        precList = null;
        items.put(new Integer(itemId), new Double(precVal));
    }

    public void add(int itemId, int precVal) {
        precList = null;
        items.put(new Integer(itemId), new Double((double) precVal));
    }

    public List<Integer> determinePrecedenceList() {
        if (precList != null && !precList.isEmpty()) {
            return precList;
        }
        if (items == null || items.isEmpty()) {
            return null;
        }

        precList = new ArrayList<Integer>();

        for (Integer itemId : items.keySet()) {
            precList.add(itemId);
        }
        Collections.sort(precList, new PrecedenceComp(items, ascending));

        highestPrecList = new ArrayList<Integer>();
        Double topValue = null;
        for (Integer itemId : precList) {
            Double value = items.get(itemId);
            if (topValue == null) {
                topValue = value;
                highestPrecList.add(itemId);
            } else {
                if (Math.abs(topValue.doubleValue() - value.doubleValue()) < Precedence.tolerance) {
                    highestPrecList.add(itemId);
                }
            }
        }
        return precList;
    }

    public Integer determineHighestPrecedenceItem() {
        determinePrecedenceList();
        if (precList != null && !precList.isEmpty()) {
            return precList.get(0);
        }
        return null;
    }

    public boolean hasMultipleHighestPrecedenceItems() {
        determinePrecedenceList();
        return highestPrecList.size() > 1;
    }

    public List<Integer> determineHighestPrecedenceList() {
        determinePrecedenceList();
        return highestPrecList;
    }

    public Double get(Integer itemId) {
        if (items == null) {
            return null;
        }
        return items.get(itemId);
    }

    public int size() {
        if (items == null) {
            return 0;
        }
        return items.size();
    }
}
