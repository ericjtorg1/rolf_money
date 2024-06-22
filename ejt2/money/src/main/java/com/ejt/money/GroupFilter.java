package com.ejt.money;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupFilter extends TransactionFilter {

    public enum AmountLabel {
        Q1, Q2, Q3, Q4, Total
    }

    private Map<AmountLabel, Integer> amountsMap = new HashMap<>();

    private List<TransactionFilter> filters = new ArrayList<>();

    public List<TransactionFilter> getFilters() {
        return filters;
    }

    public Integer getAmount(AmountLabel label) {
        return amountsMap.get(label);
    }

    public void setAmount(AmountLabel label, int amount) {
        amountsMap.put(label, amount);
    }


}
