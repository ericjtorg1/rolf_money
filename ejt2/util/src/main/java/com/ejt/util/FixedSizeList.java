package com.ejt.util;

import java.util.ArrayList;

public class FixedSizeList<T> extends ArrayList<T> {

    private static final long serialVersionUID = -2978008525770164980L;

    private final int maxSize;

    public FixedSizeList(int maxSize) {
        super();
        this.maxSize = maxSize;
    }

    @Override
    public boolean add(T t) {
        if (size() >= maxSize) {
            remove(0);
        }
        return super.add(t);
    }
}
