package com.ejt.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CalendarUtil {

    private static String[] patterns = {"yyyy-MM-dd HH:mm:ss.SSS", "yyyy-MM-dd' 'HH:mm:ss.SSSz", "dd.MM.yy",
            "dd/MM/yy", "yyyy.MM.dd G 'at' hh:mm:ss z", "EEE, MMM d, ''yy", "yyyy-MM-dd", "h:mm a", "H:mm",
            "H:mm:ss:SSS", "K:mm a,z", "yyyy.MMMMM.dd GGG hh:mm aaa"};

    private static String timeZone = null;

    private CalendarUtil() {
    }

    public static Calendar parse(String time, String pattern) {
        if (time == null) {
            throw new IllegalArgumentException("Invalid time");
        }
        if (pattern == null) {
            throw new IllegalArgumentException("Invalid pattern");
        }
        Calendar cal;
        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        try {
            date = sdf.parse(time);
            cal = Calendar.getInstance();
            cal.setTime(date);
            return cal;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Failed to parse time string: " + e.getMessage());
        }
    }

    public static Calendar parse(String time) {
        if (time == null) {
            throw new IllegalArgumentException("Invalid time");
        }
        Calendar cal;
        Date date = null;
        for (int i = 0; i < patterns.length; i++) {
            SimpleDateFormat sdf = new SimpleDateFormat(patterns[i]);
            sdf.setLenient(false);
            try {
                date = sdf.parse(time);
            } catch (ParseException e) {
                date = null;
            }
            if (date != null) {
                cal = Calendar.getInstance();
                cal.setTime(date);
                return cal;
            }
        }
        throw new IllegalArgumentException("Failed to parse time string: " + time);
    }

    public static Calendar parse(Timestamp s) {
        if (s == null) {
            return null;
        }
        return parse(s.toString());
    }

    public static Date toDate(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return calendar.getTime();
    }

    public static Timestamp toTimestamp(Calendar calendar) {
        if (calendar == null) {
            return null;
        }
        return new Timestamp(calendar.getTimeInMillis());
    }

    public static String toString(Calendar calendar, String format) {
        if (calendar == null) {
            return "null";
        }
        SimpleDateFormat dsf = new SimpleDateFormat(format);
        String dateString = dsf.format(calendar.getTime());
        return dateString;
    }

    public static String toString(Date date, String format) {
        if (date == null) {
            return "null";
        }
        SimpleDateFormat dsf = new SimpleDateFormat(format);
        String dateString = dsf.format(date);
        return dateString;
    }

    public static int convertMonth(String month) {
        if ("Jan".equalsIgnoreCase(month))
            return 1;
        else if ("Feb".equalsIgnoreCase(month))
            return 2;
        else if ("Mar".equalsIgnoreCase(month))
            return 3;
        else if ("Apr".equalsIgnoreCase(month))
            return 4;
        else if ("May".equalsIgnoreCase(month))
            return 5;
        else if ("Jun".equalsIgnoreCase(month))
            return 6;
        else if ("Jul".equalsIgnoreCase(month))
            return 7;
        else if ("Aug".equalsIgnoreCase(month))
            return 8;
        else if ("Sep".equalsIgnoreCase(month))
            return 9;
        else if ("Oct".equalsIgnoreCase(month))
            return 10;
        else if ("Nov".equalsIgnoreCase(month))
            return 11;
        else if ("Dec".equalsIgnoreCase(month))
            return 12;
        return -1;
    }

    public static boolean isValidDate(String date, String format) {
        try {
            parse(date, format);
            return true;
        } catch (IllegalArgumentException iae) {
        }
        return false;
    }

    public static String getTimeZone() {
        return timeZone;
    }

    public static void setTimeZone(String timeZone) {
        CalendarUtil.timeZone = timeZone;
    }
}
