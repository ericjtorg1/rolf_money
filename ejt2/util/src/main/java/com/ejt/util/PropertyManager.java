package com.ejt.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang3.StringUtils;

public class PropertyManager {

    private static String basePath;

    static {
        getBasePath();
    }

    private static Properties props = null;

    private static String propFile = null;

    public static void setPropFile(String propFile) {
        PropertyManager.propFile = propFile;
    }

    public static int size() {
        return (props == null) ? 0 : props.size();
    }

    private static String getProperty(String key) {
        if (props == null) {
            props = new Properties();
            load();
        }
        if (key == null) {
            return null;
        }
        return (String) props.get(key);
    }

    public static boolean getBoolean(String key, boolean defl) {
        String value = getProperty(key);
        if (value == null) {
            return defl;
        } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) {
            return true;
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no")) {
            return false;
        }
        return defl;
    }

    public static String getString(String key, String defl) {
        String value = getProperty(key);
        if (value == null) {
            return defl;
        }
        return value;
    }

    public static String getFile(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        return getBasePath() + value;
    }

    public static long getLong(String key, long defl) {
        String value = getProperty(key);
        if (value == null)
            return defl;
        long retVal;
        try {
            retVal = Long.parseLong(value);
        } catch (NumberFormatException nfe) {
            retVal = defl;
        }
        return retVal;
    }

    public static int getInt(String key, int defl) {
        String value = getProperty(key);
        if (value == null)
            return defl;
        int retVal;
        try {
            retVal = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            retVal = defl;
        }
        return retVal;
    }

    public static Integer getIntObj(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static float getFloat(String key, float defl) {
        String value = getProperty(key);
        if (value == null)
            return defl;
        float retVal;
        try {
            retVal = Float.parseFloat(value);
        } catch (NumberFormatException nfe) {
            retVal = defl;
        }
        return retVal;
    }

    public static double getDouble(String key, double defl) {
        String value = getProperty(key);
        if (value == null)
            return defl;
        double retVal;
        try {
            retVal = Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            retVal = defl;
        }
        return retVal;
    }

    public static Double getDoubleObj(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

    public static void setDefaults(Properties properties) {
        if (props == null) {
            reload();
        }
        for (Object key : properties.keySet()) {
            if (!props.containsKey(key)) {
                props.put(key, properties.get(key));
            }
        }
    }

    public static String[] getStrings(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        StringTokenizer parts = new StringTokenizer(value, ",");
        List<String> strings = new ArrayList<String>();
        while (parts.hasMoreTokens()) {
            strings.add(parts.nextToken());
        }

        if (strings.isEmpty()) {
            return null;
        }

        int i = 0;
        String[] stringArray = new String[strings.size()];
        for (String s : strings) {
            stringArray[i++] = s;
        }
        return stringArray;
    }

    public static Set<String> getStringsAsSet(String key) {
        Set<String> set = new HashSet<String>();
        String[] strings = getStrings(key);
        if (strings == null) {
            return set;
        }
        for (String s : strings) {
            if (StringUtils.isNotBlank(s)) {
                set.add(s);
            }
        }
        return set;
    }

    public static List<String> getStringList(String key) {
        List<String> strings = new ArrayList<String>();
        int count = 1;
        String value;
        for (; ; ) {
            value = getProperty(key + "." + count);
            if (value == null) {
                break;
            }
            strings.add(value);
            count++;
        }
        return strings;
    }

    public static String[] getStringArray(String key) {
        List<String> strings = getStringList(key);
        int count = 0;
        String[] stringArray = new String[strings.size()];
        for (String s : strings) {
            stringArray[count++] = s;
        }
        return stringArray;
    }

    public static synchronized void reload() {
        if (props == null) {
            props = new Properties();
        }
        load();
    }

    private static synchronized void load() {
        try {
            props.clear();
            String propPath = null;
            if (!isPropFilePathDefined()) {
                System.out.println("load: no properties file path provided as system property");
                if (propFile == null) {
                    return;
                }
                propPath = propFile;
            } else {
                propPath = getPropFilePath();
                if (propFile != null) {
                    propPath += File.separator + propFile;
                }
            }
            // logger.debug("load: opening properties file " + propPath);
            props = getProperties(propPath);
        } catch (Exception ex) {
            System.err.println("load: error in loading properties");
            System.err.println(ex);
        }
    }

    public static boolean isPropFilePathDefined() {
        boolean found = false;
        try {
            String value = System.getProperty("prop_file_path");
            if (value != null && value.length() > 0) {
                found = true;
            }
        } catch (Exception e) {
            System.err.println("isPropFileDefined: error getting prop_file_path: " + e.getMessage());
        }
        return found;
    }

    public static String getPropFilePath() {
        String value = "";
        try {
            value = System.getProperty("prop_file_path");
            if (value == null || value.length() == 0) {
                value = "";
            }
        } catch (Exception e) {
            System.err.println("getPropPath: error getting prop_file_path: " + e.getMessage());
        }
        return value;
    }

    private static Properties getProperties(String name) {
        Properties prop = new Properties();
        InputStream is = null;
        try {
            // first try to load as resource stream
            PropertyManager pm = new PropertyManager();
            if (pm.getClass().getClassLoader() == null) {
                throw new Exception("ClassLoader for PropertyManager is null");
            }
            is = pm.getClass().getClassLoader().getResourceAsStream(name);
            if (is == null) {
                throw new Exception("InputStream for PropertyManager for " + name + " is null");
            }
            prop.load(is);
        } catch (Exception ex) {
            // System.out.println("getProperties: " + ex.getMessage());
            // try to load as a file
            try {
                is = new FileInputStream(name);
                prop.load(is);
            } catch (Exception e) {
                System.err.println("getProperties: error in loading properties from " + name);
                System.err.println(ex);
            }
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception ex) {
                }
            }
        }
        return prop;
    }

    private static String getBasePath() {
        if (basePath != null) {
            return basePath;
        }
        basePath = "";
        try {
            basePath = System.getProperty("base_path");
            if (basePath == null) {
                basePath = "";
            }
        } catch (Exception e) {
            System.err.println("getPropPath: error getting base_path: " + e.getMessage());
        }
        return basePath;
    }
}
