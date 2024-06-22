package com.ejt.util;

import org.apache.commons.lang3.StringUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class Util {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Util.class);

    public static String pathPrefix = "";

    private Util() {
    }

    public static String replacePattern(String buf, String inPattern1, String outPattern1, String inPattern2,
                                        String outPattern2) {
        return replacePattern(replacePattern(buf, inPattern1, outPattern1), inPattern2, outPattern2);
    }

    public static String replacePattern(String buf, String inPattern, String outPattern) {
        if (buf == null || inPattern == null || outPattern == null || buf.indexOf(inPattern) == -1) {
            return buf;
        }
        StringBuilder sb = new StringBuilder(buf.length());
        int startIndex = 0;
        int index = buf.indexOf(inPattern);
        int inPattLeng = inPattern.length();
        while (index != -1) {
            index += startIndex;
            sb.append(buf.substring(startIndex, index));
            sb.append(outPattern);
            startIndex = index + inPattLeng;
            index = buf.substring(startIndex).indexOf(inPattern);
        }
        if (startIndex < buf.length()) {
            sb.append(buf.substring(startIndex));
        }
        return sb.toString();
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

    public static boolean isNotNull(Object obj) {
        return obj != null;
    }

    public static boolean isNotNullGreaterThan(Integer value, int compare, boolean equalsTrue) {
        if (value == null) {
            return false;
        }
        if (value.intValue() == compare) {
            return equalsTrue;
        }
        return value.intValue() > compare;
    }

    public static boolean isNotNullLesserThan(Integer value, int compare, boolean equalsTrue) {
        if (value == null) {
            return false;
        }
        if (value.intValue() == compare) {
            return equalsTrue;
        }
        return value.intValue() < compare;
    }

    public static String toStringObject(Object obj) {
        if (obj == null) {
            return "null";
        }
        return obj.toString();
    }

    public static String toStringList(List<?> list) {
        if (list == null) {
            return "null";
        } else if (list.isEmpty()) {
            return "empty";
        }
        StringBuilder buf = new StringBuilder();
        int i = 1;
        for (Object obj : list) {
            if (i > 1) {
                buf.append(" ");
            }
            buf.append(i);
            buf.append("..");
            if (obj != null) {
                buf.append(obj.toString());
            } else {
                buf.append("null");
            }
            i++;
        }
        return buf.toString();
    }

    public static String toStringSet(Set<?> set) {
        if (set == null) {
            return "null";
        } else if (set.isEmpty()) {
            return "empty";
        }
        StringBuilder buf = new StringBuilder();
        int i = 1;
        for (Object obj : set) {
            if (i > 1) {
                buf.append(" ");
            }
            buf.append(i);
            buf.append("..");
            if (obj != null) {
                buf.append(obj.toString());
            } else {
                buf.append("null");
            }
            i++;
        }
        return buf.toString();
    }

    public static String toStringArray(String[] array) {
        if (array == null) {
            return "null";
        } else if (array.length == 0) {
            return "empty";
        }
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buf.append(" ");
            }
            buf.append(i + 1);
            buf.append("..");
            if (array[i] != null) {
                buf.append(array[i]);
            } else {
                buf.append("null");
            }
        }
        return buf.toString();
    }

    public static String toStringMap(Map<?, ?> map) {
        if (map == null) {
            return "null";
        } else if (map.isEmpty()) {
            return "empty";
        }
        StringBuilder buf = new StringBuilder();
        int i = 1;
        for (Entry<?, ?> es : map.entrySet()) {
            if (i > 1) {
                buf.append(" ");
            }
            buf.append(i);
            buf.append("..");
            if (es.getKey() != null) {
                buf.append(es.getKey().toString());
                buf.append("=");
                if (es.getValue() == null) {
                    buf.append("null");
                } else {
                    buf.append(es.getValue().toString());
                }
            } else {
                buf.append("null");
            }
            i++;
        }
        return buf.toString();
    }

    public static String format(int value, int numDigits) {
        return format(value, numDigits, " ");
    }

    public static String format(int value, int numDigits, String padChar) {
        if (numDigits <= 0 || padChar == null) {
            return "#";
        }
        StringBuilder sb = new StringBuilder();
        String buf = String.valueOf(value);
        if (buf.length() > numDigits) {
            for (int i = 0; i < numDigits; i++) {
                sb.append("#");
            }
        } else {
            for (int i = 0; i < (numDigits - buf.length()); i++) {
                sb.append(padChar);
            }
            sb.append(buf);
        }
        return sb.toString();
    }

    public static boolean getBoolean(String value, boolean defl) {
        if (value == null) {
            return defl;
        } else if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")) {
            return true;
        } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no")) {
            return false;
        }
        return defl;
    }

    public static int getInt(String value, int defl) {
        if (value == null) {
            return defl;
        }
        int retVal;
        try {
            retVal = Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            retVal = defl;
        }
        return retVal;
    }

    public static double getDouble(String value, double defl) {
        if (value == null) {
            return defl;
        }
        double retVal;
        try {
            retVal = Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            retVal = defl;
        }
        return retVal;
    }

    // drive should be like "D:" or "E:"
    public static String getDriveName(String drive) throws Exception {
        if (StringUtils.isBlank(drive)) {
            throw new Exception("No valid drive provided");
        }

        FileSystemView fsv = FileSystemView.getFileSystemView();
        if (fsv == null) {
            throw new Exception("Failed to get FileSystemView");
        }

        File[] roots = File.listRoots();
        if (roots == null || roots.length <= 0) {
            throw new Exception("Failed to get any file system roots");
        }

        String buf, name, driveName = null;
        for (int i = 0; i < roots.length; i++) {
            buf = "(" + drive + ")";
            name = fsv.getSystemDisplayName(roots[i]);
            if (name != null && name.indexOf(buf) >= 0) {
                driveName = StringUtils.trim(name.substring(0, name.length() - buf.length()));
                if (driveName == null) {
                    return "";
                }
            }
        }
        if (driveName == null) {
            throw new Exception("No drive " + drive + " exists");
        }
        return driveName;
    }

    public static void appendPadString(StringBuilder sb, int val, Integer size, boolean frontPad) {
        if (size == null) {
            appendPadString(sb, val, 0, frontPad);
        }
        appendPadString(sb, val, size.intValue(), frontPad);
    }

    public static void appendPadString(StringBuilder sb, int val, int size, boolean frontPad) {
        if (sb == null) {
            return;
        }
        int sizeVal = String.valueOf(val).length();
        if (sizeVal >= size) {
            sb.append(val);
            return;
        }
        int diff = size - sizeVal;
        if (frontPad) {
            for (int i = 0; i < diff; i++) {
                sb.append(" ");
            }
            sb.append(val);
            return;
        }
        sb.append(val);
        for (int i = 0; i < diff; i++) {
            sb.append(" ");
        }
    }

    public static void appendPadString(StringBuilder sb, String val, Integer size, boolean frontPad) {
        if (size == null) {
            appendPadString(sb, val, 0, frontPad);
        }
        appendPadString(sb, val, size.intValue(), frontPad);
    }

    public static String padString(int val, int size, boolean frontPad, String pad) {
        StringBuilder sb = new StringBuilder();
        appendPadString(sb, String.valueOf(val), size, frontPad, pad);
        return sb.toString();
    }

    public static String padString(String val, int size, boolean frontPad, String pad) {
        StringBuilder sb = new StringBuilder();
        appendPadString(sb, val, size, frontPad, pad);
        return sb.toString();
    }

    public static void appendPadString(StringBuilder sb, String val, int size, boolean frontPad) {
        appendPadString(sb, val, size, frontPad, " ");
    }

    public static void appendPadString(StringBuilder sb, String val, int size, boolean frontPad, String pad) {
        if (sb == null || val == null) {
            return;
        }
        int sizeVal = val.length();
        if (sizeVal >= size) {
            sb.append(val);
            return;
        }
        int diff = size - sizeVal;
        if (frontPad) {
            for (int i = 0; i < diff; i++) {
                sb.append(pad);
            }
            sb.append(val);
            return;
        }
        sb.append(val);
        for (int i = 0; i < diff; i++) {
            sb.append(pad);
        }
    }

    public static boolean isEqual(String str1, String str2) {
        if (str1 == null && str2 == null) {
            return true;
        } else if (str1 == null || str2 == null) {
            return false;
        }
        return str1.equals(str2);
    }

    public static boolean isEqual(Integer int1, Integer int2) {
        if (int1 == null && int2 == null) {
            return true;
        } else if (int1 == null || int2 == null) {
            return false;
        }
        return int1.intValue() == int2.intValue();
    }

    public static boolean isEqual(Integer int1, int int2) {
        if (int1 == null) {
            return false;
        }
        return int1.intValue() == int2;
    }

    public static boolean isEqual(int int1, int int2) {
        return int1 == int2;
    }

    public static boolean isGreater(int int1, int int2) {
        return int1 > int2;
    }

    public static boolean isGreaterEqual(int int1, int int2) {
        return int1 >= int2;
    }

    public static final double tolerance = 0.0001;

    public static boolean isEqual(Double dbl1, Double dbl2) {
        if (dbl1 == null && dbl2 == null) {
            return true;
        } else if (dbl1 == null || dbl2 == null) {
            return false;
        }
        return isEqual(dbl1.doubleValue(), dbl2.doubleValue());
    }

    public static final boolean isEqual(double v1, double v2) {
        return Math.abs(v1 - v2) < tolerance;
    }

    public static final boolean isLessThanOrEqual(double v1, double v2) {
        if (isEqual(v1, v2)) {
            return true;
        }
        return v1 < v2;
    }

    public static final boolean isLessThan(double v1, double v2) {
        if (isEqual(v1, v2)) {
            return false;
        }
        return v1 < v2;
    }

    public static final boolean isGreaterThanOrEqual(double v1, double v2) {
        if (isEqual(v1, v2)) {
            return true;
        }
        return v1 > v2;
    }

    public static final boolean isGreaterThan(double v1, double v2) {
        if (isEqual(v1, v2)) {
            return false;
        }
        return v1 > v2;
    }

    public static final boolean isInDelimitedList(String value, String delimitedList, String delimit) {
        if (value == null || delimitedList == null || delimit == null) {
            return false;
        }
        if (value.equals(delimitedList)) {
            return true;
        } else if (delimitedList.startsWith(value + delimit)) {
            return true;
        } else if (delimitedList.endsWith(delimit + value)) {
            return true;
        } else if (delimitedList.indexOf(delimit + value + delimit) > 0) {
            return true;
        }
        return false;
    }

    public static final List<String> parseQuotedList2(String listLine) {
        if (listLine == null) {
            throw new IllegalArgumentException("Invalid listLine");
        }

        Set<Integer> replaceIdx = new HashSet<>();
        for (int i = 1; i < (listLine.length() - 1); i++) {
            if (listLine.charAt(i) == ',' && listLine.charAt(i - 1) != '"' && listLine.charAt(i + 1) != '"') {
                replaceIdx.add(Integer.valueOf(i));
            }
        }

        String line = listLine;
        if (!replaceIdx.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < listLine.length(); i++) {
                if (replaceIdx.contains(Integer.valueOf(i))) {
                    sb.append(';');
                } else {
                    sb.append(listLine.charAt(i));
                }
            }
            line = sb.toString();
        }

        List<String> list2 = new ArrayList<>();
        List<String> list = convertToStringList(line);
        for (String item : list) {
            if (item.startsWith("\"") && item.endsWith("\"")) {
                list2.add(item.substring(1, item.length() - 1));
            } else {
                throw new IllegalArgumentException("Invalid listLine: all items not quoted");
            }
        }
        return list2;
    }

    public static final List<String> parseQuotedList(String listLine) {
        if (listLine == null) {
            throw new IllegalArgumentException("Invalid listLine");
        }

        int startIndex = 0, index, count = 0;
        List<String> list = new ArrayList<>();

        while ((index = listLine.indexOf("\"", startIndex)) >= 0) {
            count++;
            if ((count % 2) == 0) {
                list.add(listLine.substring(startIndex, index));
            }
            startIndex = index + 1;
        }
        if ((count % 2) != 0) {
            throw new IllegalArgumentException("Invalid listLine");
        }

        return list;
    }

    public static String getEnvironmentVariableValue(String name, String defl) {
        Map<String, String> env = System.getenv();
        if (env != null) {
            String value = env.get(name);
            if (value != null) {
                return value;
            }
        }
        return defl;
    }

    private final static Random random = new Random();

    public static int getRandomValue(int range) {
        return random.nextInt(range);
    }

    public static <X> void shuffleList(List<X> list) {
        long seed = System.nanoTime();
        Collections.shuffle(list, new Random(seed));
    }

    public final static String NULL = "NULL";

    public static String produceNull(String value) {
        if (value == null) {
            return NULL;
        }
        return value;
    }

    public static String handleNull(String value) {
        if (NULL.equals(value)) {
            return null;
        }
        return value;
    }

    public static List<String> convertToStringList(String value) {
        return convertToStringList(value, ",");
    }

    public static List<String> convertToStringList(String value, String delim) {
        List<String> list = new ArrayList<String>();
        if (StringUtils.isBlank(value)) {
            return list;
        }
        StringTokenizer parts = new StringTokenizer(value, delim);
        while (parts.hasMoreTokens()) {
            list.add(parts.nextToken());
        }
        return list;
    }

    public static <T> String convertToString(List<T> list) {
        return convertToString(list, ",");
    }

    public static <T> String convertToString(List<T> list, String delim) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        boolean firstOne = true;
        StringBuilder sb = new StringBuilder();
        for (T v : list) {
            if (!firstOne) {
                sb.append(delim);
            }
            firstOne = false;
            sb.append(v);
        }
        return sb.toString();
    }

    public static <T> String truncateMiddle(List<T> list, int showSize) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        if (list.size() <= showSize) {
            return convertToString(list, " ");
        }
        int half = showSize / 2;
        int upperHalf = list.size() - half + 1;

        StringBuffer buf = new StringBuffer();
        int count = 0;
        for (T v : list) {
            count++;
            if ((count > half) && (count < upperHalf)) {
                if (count == (half + 1)) {
                    buf.append(" ..").append(list.size()).append("..");
                }
                continue;
            }
            if (count > 1) {
                buf.append(" ");
            }
            buf.append(v);
        }
        return buf.toString();
    }

    public static Set<String> convertToStringSet(String value) {
        return convertToStringSet(value, ",");
    }

    public static Set<String> convertToStringSet(String value, String delim) {
        Set<String> set = new HashSet<>();
        if (StringUtils.isBlank(value)) {
            return set;
        }
        StringTokenizer parts = new StringTokenizer(value, delim);
        while (parts.hasMoreTokens()) {
            set.add(parts.nextToken());
        }
        return set;
    }

    public static Set<Integer> convertToIntegerSet(String value) {
        Set<Integer> set = new HashSet<>();
        if (StringUtils.isBlank(value)) {
            return set;
        }
        StringTokenizer parts = new StringTokenizer(value, ",");
        while (parts.hasMoreTokens()) {
            Integer v = Integer.valueOf(parts.nextToken());
            set.add(v);
        }
        return set;
    }

    public static List<Integer> convertToIntegerList(String value) {
        List<Integer> list = new ArrayList<>();
        if (StringUtils.isBlank(value)) {
            return list;
        }
        StringTokenizer parts = new StringTokenizer(value, ",");
        while (parts.hasMoreTokens()) {
            Integer v = Integer.valueOf(parts.nextToken());
            list.add(v);
        }
        return list;
    }


    public static <T> String convertToString(Set<T> set) {
        if (set == null || set.isEmpty()) {
            return null;
        }
        boolean firstOne = true;
        StringBuilder sb = new StringBuilder();
        for (T v : set) {
            if (!firstOne) {
                sb.append(",");
            }
            firstOne = false;
            sb.append(v);
        }
        return sb.toString();
    }

    public static <T> boolean areEqual(Set<T> set1, Set<T> set2) {
        if (set1 == null && set2 == null) {
            return true;
        } else if (set1 == null || set2 == null) {
            return false;
        }
        if (set1.size() != set2.size()) {
            return false;
        }
        if (set1.isEmpty()) {
            return true;
        }
        for (T e : set1) {
            if (!set2.contains(e)) {
                return false;
            }
        }
        for (T e : set2) {
            if (!set1.contains(e)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");
    }


    public static List<String> readFile(String filePath) {
        List<String> fileLines = new ArrayList<>();
        BufferedReader fis = null;
        int lineNum = 0;
        try {
            File file = new File(pathPrefix + filePath);
            if (!file.exists()) {
                logger.error("File file does not exist: " + pathPrefix + filePath);
                System.exit(1);
            }
            fis = new BufferedReader(new FileReader(file));
            while (fis.ready()) {
                lineNum++;
                String line = fis.readLine();
                if (line == null) {
                    break;
                }
                fileLines.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ioe) {
                logger.warn("IOException closing file: " + ioe.getMessage());
            }
        }
        logger.debug(filePath + " #lines=" + fileLines.size());
        return fileLines;
    }

    public static void writeFile(String filePath, List<String> fileLines) {
        writeFile(filePath, fileLines, false);
    }

    public static void writeFile(String filePath, List<String> fileLines, boolean append) {
        BufferedWriter fos = null;
        try {
            File file = new File(pathPrefix + filePath);
            fos = new BufferedWriter(new FileWriter(file, append));

            for (String line : fileLines) {
                fos.write(line);
                fos.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ioe) {
                logger.warn("IOException closing file: " + ioe.getMessage());
            }
            logger.debug(filePath + " #lines=" + fileLines.size());
        }
    }

    public static void setPathPrefix(String pathPrefix) {
        Util.pathPrefix = pathPrefix;
    }
}
