package com.cisdi.cpm.auth.utils;

import com.cisdi.cpm.auth.exception.BaseException;
import com.cisdi.cpm.auth.exception.SysException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class DateUtils {
    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    //The first UTC format (example��2014-11-11T14:00:00+08:00)
    private static final String UTC_PATTERN_1 = "yyyy-MM-dd'T'HH:mm:ss+HH:mm";
    //The second UTC format��example��2014-11-07T14:00:00Z��
    private static final String UTC_PATTERN_2 = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String DATE_PATTERN = "yyyy-MM-dd";
    //The EST��CST��GMT format
    //	example��Tue Jan 05 00:00:00 EST 2015;
    //			 Tue Jan 05 00:00:00 CST 2015;
    //			 Mon Jan 05 13:00:00 GMT+08:00 2015
    private static final String CST_PATTERN = "EEE MMM dd HH:mm:ss zzz yyyy";

    public static java.sql.Date getSqlDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * Parse date string according to different region
     *
     * @param dateStr
     * 		Date string
     * @param format
     *  	The format of dateStr
     * @return
     */
    public static Date parseDateLocale(String dateStr, String format) throws BaseException {
        SimpleDateFormat sdf = null;
        Date date = null;
        if (dateStr == null || "null".equals(dateStr)) {
            return null;
        }
        if (dateStr.contains("CST") || dateStr.contains("EST") || dateStr.contains("GMT")||dateStr.contains("EDT") ) {
            sdf = new SimpleDateFormat(CST_PATTERN, Locale.US);
        }
        else if (dateStr.contains("T") && dateStr.contains("Z")) {
            sdf = new SimpleDateFormat(UTC_PATTERN_2);
        }
        else if (dateStr.contains("T")) {
            sdf = new SimpleDateFormat(UTC_PATTERN_1);
        }
        else {
            sdf = new SimpleDateFormat(format);
        }

        try {
            date = sdf.parse(dateStr);
        }
        catch (ParseException e) {
            e.printStackTrace();
            throw new SysException(e);
        }

        return date;
    }

    /**
     * Format date string
     *
     * @param dateStr
     * 		Date string
     * @param preFormat
     * 		The date format of dateStr
     * @param suFormat
     * 		The date format of result
     * @return
     * @throws BaseException
     */
    public static String formatDate(String dateStr, String preFormat, String suFormat)
            throws BaseException {
        if (dateStr == null || "null".equals(dateStr)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(suFormat);
        String result = "";
        try {
            result = sdf.format(parseDateLocale(dateStr, preFormat));
        } catch (Exception ex) {
            throw new SysException(ex);
        }
        return result;
    }

    /**
     * Get the date after afterDay for currentDate
     *
     * @param currentDate
     * @param afterDay
     * @return
     * @throws BaseException
     */
    public static Date getDateAfterDay(Date currentDate, int afterDay) throws BaseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + afterDay);
        return cal.getTime();
    }

    /**
     * Format date
     *
     * @param date
     * @param format
     * @return
     * @throws BaseException
     */
    public static String formatDate(Date date, String format) throws BaseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * convert timeStamp into formatted date string
     *
     * @param timeStamp
     * @param format
     * @return
     * @throws BaseException
     */
    public static String parseFromTimeStamp(Long timeStamp, String format)
            throws BaseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = new Date(timeStamp);

        return sdf.format(date);
    }

    /**
     * Convert object into Date
     *
     * @param obj
     * @return
     */
    public static Date obj2Date(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Date) {
            return (Date) obj;
        }
        try {
            return str2Date(String.valueOf(obj));
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Parse date string to date
     *
     * @param str
     * @return
     * @throws ParseException
     */
    public static Date str2Date(String str) throws ParseException {
        if (str == null) {
            return null;
        }
        DateFormat sdf = new SimpleDateFormat(DEFAULT_PATTERN);
        Date date = null;
        try {
            date = sdf.parse(str);
        } catch ( ParseException e1 ) {
            try {
                date = UTC2Date(str);
            } catch ( ParseException e2 ) {
                date = dateStr2Date(str);
            }
        }
        return date;
    }

    /**
     * Convert UTC format date string into date
     *
     * @param utc
     * @return
     * @throws ParseException
     */
    public static Date UTC2Date(String utc) throws ParseException {
        if (utc == null) {
            return null;
        }
        DateFormat sdf = new SimpleDateFormat(UTC_PATTERN_1);
        Date date = sdf.parse(utc);
        return date;
    }

    /**
     * Simple string to date
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static Date dateStr2Date(String dateStr) throws ParseException {
        if (dateStr == null) {
            return null;
        }
        DateFormat sdf = new SimpleDateFormat(DATE_PATTERN);
        Date date = sdf.parse(dateStr.substring(0, DATE_PATTERN.length()));
        return date;
    }

    //date validation
    public static boolean isDate(String date_str) throws BaseException {
        //date format
        String req_date =
                "((^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])(10|12|0?[13578])"
                        + "([-\\/\\._])(3[01]|[12][0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|"
                        + "([2-9]\\d{3}))([-\\/\\._])(11|0?[469])([-\\/\\._])(30|[12]"
                        + "[0-9]|0?[1-9])$)|(^((1[8-9]\\d{2})|([2-9]\\d{3}))([-\\/\\._])"
                        + "(0?2)([-\\/\\._])(2[0-8]|1[0-9]|0?[1-9])$)|(^([2468][048]00)"
                        + "([-\\/\\._])(0?2)([-\\/\\._])(29)$)|(^([3579][26]00)([-\\/\\._])"
                        + "(0?2)([-\\/\\._])(29)$)|(^([1][89][0][48])([-\\/\\._])(0?2)"
                        + "([-\\/\\._])(29)$)|(^([2-9][0-9][0][48])([-\\/\\._])(0?2)"
                        + "([-\\/\\._])(29)$)|(^([1][89][2468][048])([-\\/\\._])(0?2)"
                        + "([-\\/\\._])(29)$)|(^([2-9][0-9][2468][048])([-\\/\\._])(0?2)"
                        + "([-\\/\\._])(29)$)|(^([1][89][13579][26])([-\\/\\._])(0?2)"
                        + "([-\\/\\._])(29)$)|(^([2-9][0-9][13579][26])([-\\/\\._])(0?2)"
                        + "([-\\/\\._])(29)$))";
        Pattern pat = Pattern.compile(req_date);
        if (pat.matcher(date_str).matches()) {
            return true;
        }
        else {
            return false;
        }
    }
}

