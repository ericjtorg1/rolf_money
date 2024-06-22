package com.ejt.util;

import java.util.*;

public class StackMap<D> {

    private int size, index;
    private Map<String, D> elementMap;
    private String[] keyArray;

    public StackMap(int size) {
        if (size <= 0) {
            throw new RuntimeException("StackMap size must be greater than 0");
        }
        this.size = size;
        index = 0;
        keyArray = new String[size];
        elementMap = new HashMap<String, D>();
    }

    public void clear() {
        index = 0;
        elementMap.clear();
        for (int i = 0; i < keyArray.length; i++) {
            keyArray[i] = null;
        }
    }

    public void put(String key, D element) {
        if (key == null) {
            throw new RuntimeException("StackMap does not support null keys");
        }
        if (element == null) {
            throw new RuntimeException("StackMap does not support null elements");
        }
        if (elementMap.containsKey(key)) {
            elementMap.put(key, element);
            return;
        }
        if (elementMap.size() < size) {
            keyArray[index] = new String(key);
            elementMap.put(key, element);
            index++;
            return;
        }
        if (index >= size) {
            index = 0;
        }
        // remove old element
        if (keyArray[index] != null) {
            elementMap.remove(keyArray[index]);
        }
        keyArray[index] = new String(key);
        elementMap.put(key, element);
        index++;
    }

    public D get(String key) {
        return elementMap.get(key);
    }

    public int size() {
        return elementMap.size();
    }

    public boolean containsKey(String key) {
        if (key == null) {
            throw new RuntimeException("StackMap does not support null keys");
        }
        return elementMap.containsKey(key);
    }

    public boolean containsValue(String value) {
        return elementMap.containsValue(value);
    }

    public boolean isEmpty() {
        return elementMap.isEmpty();
    }

    public List<String> getLastKeys(int count) {
        List<String> list = new ArrayList<String>();
        for (int i = (index - 1); i >= 0; i--) {
            if (keyArray[i] == null) {
                return list;
            }
            if (list.size() < count) {
                list.add(keyArray[i]);
            } else {
                break;
            }
        }
        for (int i = (size - 1); i >= index; i--) {
            if (keyArray[i] == null) {
                return list;
            }
            if (list.size() < count) {
                list.add(keyArray[i]);
            } else {
                break;
            }
        }
        return list;
    }

    public Collection<D> values() {
        return elementMap.values();
    }

    public Set<String> keySet() {
        return elementMap.keySet();
    }

    public List<D> getLastValues(int count) {
        List<D> list = new ArrayList<D>();
        for (int i = (index - 1); i >= 0; i--) {
            if (keyArray[i] == null) {
                return list;
            }
            if (list.size() < count) {
                list.add(elementMap.get(keyArray[i]));
            } else {
                break;
            }
        }
        for (int i = (size - 1); i >= index; i--) {
            if (keyArray[i] == null) {
                return list;
            }
            if (list.size() < count) {
                list.add(elementMap.get(keyArray[i]));
            } else {
                break;
            }
        }
        return list;
    }

    public StackMap<D> filterCopy(Set<String> removeKeys) {
        StackMap<D> copy = new StackMap(size);

        for (int i = 0; i < keyArray.length; i++) {
            if (keyArray[i] != null && !removeKeys.contains(keyArray[i])) {
                copy.put(keyArray[i], elementMap.get(keyArray[i]));
            }
        }
        return copy;
    }
}
