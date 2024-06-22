package com.ejt.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

public class PropertyHashMap {

    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(PropertyHashMap.class);

    private HashMap<String, Object> propMap = new HashMap<String, Object>();

    public Integer getIntObjProp(String key, Integer deflInt) {
        if (key == null) {
            return deflInt;
        }
        Object obj = propMap.get(key + ".INT");
        if (obj != null && (obj instanceof Integer)) {
            return (Integer) obj;
        }
        return deflInt;
    }

    public void setIntObjProp(String key, Integer value) {
        if (key != null) {
            propMap.put(key + ".INT", value);
        }
    }

    public int getIntProp(String key, int deflInt) {
        if (key == null) {
            return deflInt;
        }
        Object obj = propMap.get(key + ".INT");
        if (obj != null && (obj instanceof Integer)) {
            return ((Integer) obj).intValue();
        }
        return deflInt;
    }

    public void setIntProp(String key, int value) {
        if (key != null) {
            propMap.put(key + ".INT", new Integer(value));
        }
    }

    public String getStringProp(String key, String deflString) {
        if (key == null) {
            return deflString;
        }
        Object obj = propMap.get(key + ".STR");
        if (obj != null && (obj instanceof String)) {
            return (String) obj;
        }
        return deflString;
    }

    public void setStringProp(String key, String value) {
        if (key != null) {
            propMap.put(key + ".STR", value);
        }
    }

    public Double getDoubleObjProp(String key, Double deflDouble) {
        if (key == null) {
            return deflDouble;
        }
        Object obj = propMap.get(key + ".DBL");
        if (obj != null && (obj instanceof Double)) {
            return (Double) obj;
        }
        return deflDouble;
    }

    public void setDoubleObjProp(String key, Double value) {
        if (key != null) {
            propMap.put(key + ".DBL", value);
        }
    }

    public double getDoubleProp(String key, double deflDouble) {
        if (key == null) {
            return deflDouble;
        }
        Object obj = propMap.get(key + ".DBL");
        if (obj != null && (obj instanceof Double)) {
            return ((Double) obj).doubleValue();
        }
        return deflDouble;
    }

    public void setDoubleProp(String key, double value) {
        if (key != null) {
            propMap.put(key + ".DBL", new Double(value));
        }
    }

    public void copy(PropertyHashMap phm) {
        if (phm == null || phm.getNumProps() == 0) {
            return;
        }
        propMap.clear();
        String key2;
        for (String key : phm.getKeySet()) {
            if (key == null || key.length() <= 4) {
                continue;
            }
            key2 = key.substring(0, key.length() - 4);
            if (key.endsWith(".INT")) {
                setIntObjProp(key2, phm.getIntObjProp(key2, null));
            } else if (key.endsWith(".STR")) {
                setStringProp(key2, phm.getStringProp(key2, null));
            } else if (key.endsWith(".DBL")) {
                setDoubleObjProp(key2, phm.getDoubleObjProp(key2, null));
            }
        }
    }

    public boolean equals(PropertyHashMap phm) {
        if (phm == null) {
            return false;
        } else if (getNumProps() != phm.getNumProps()) {
            return false;
        } else if (getNumProps() == 0) {
            return true;
        }
        try {
            for (String key : getKeySet()) {
                if (key == null || key.length() <= 4) {
                    logger.warn("key is invalid");
                    return false;
                }
                if (key.endsWith(".INT")) {
                    if (!Util.isEqual(getIntProperty(key), phm.getIntProperty(key))) {
                        return false;
                    }
                } else if (key.endsWith(".STR")) {
                    if (!Util.isEqual(getStringProperty(key), phm.getStringProperty(key))) {
                        return false;
                    }
                } else if (key.endsWith(".DBL")) {
                    if (!Util.isEqual(getDoubleProperty(key), phm.getDoubleProperty(key))) {
                        return false;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void removeProps(String keyPrefix) {
        if (StringUtils.isBlank(keyPrefix) || propMap.isEmpty()) {
            return;
        }
        Set<String> removeKeys = new HashSet<String>();
        for (String key : propMap.keySet()) {
            if (key != null && key.startsWith(keyPrefix)) {
                removeKeys.add(key);
            }
        }
        for (String key : removeKeys) {
            if (propMap.containsKey(key)) {
                propMap.remove(key);
            }
        }
    }

    public Set<String> getKeySet() {
        return propMap.keySet();
    }

    public int getNumProps() {
        return propMap.size();
    }

    public Integer getIntProperty(String key) throws Exception {
        if (key == null) {
            throw new Exception("Invalid key");
        }
        if (!propMap.containsKey(key)) {
            throw new Exception("No property for key " + key);
        }
        Object obj = propMap.get(key);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return (Integer) obj;
        }
        throw new Exception("No integer property for key " + key);
    }

    public String getStringProperty(String key) throws Exception {
        if (key == null) {
            throw new Exception("Invalid key");
        }
        if (!propMap.containsKey(key)) {
            throw new Exception("No property for key " + key);
        }
        Object obj = propMap.get(key);
        if (obj == null) {
            return null;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        throw new Exception("No string property for key " + key);
    }

    public Double getDoubleProperty(String key) throws Exception {
        if (key == null) {
            throw new Exception("Invalid key");
        }
        if (!propMap.containsKey(key)) {
            throw new Exception("No property for key " + key);
        }
        Object obj = propMap.get(key);
        if (obj == null) {
            return null;
        }
        if (obj instanceof Double) {
            return (Double) obj;
        }
        throw new Exception("No double property for key " + key);
    }

    public String toStringMap() {
        return Util.toStringMap(propMap);
    }

    public void clear() {
        propMap.clear();
    }

    public int size() {
        return propMap.size();
    }

    ;
}
