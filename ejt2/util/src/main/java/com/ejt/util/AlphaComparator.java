package com.ejt.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;

public class AlphaComparator implements Comparator<String> {

    public int compare(String s1, String s2) {
        int comparison = -1;
        if (StringUtils.isNotEmpty(s1) && StringUtils.isNotEmpty(s2))
            comparison = s1.compareTo(s2);
        return comparison;
    }
}
