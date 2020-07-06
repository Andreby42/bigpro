package com.thc.platform.modules.ocr.util;

import org.apache.commons.lang.time.DateUtils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    private static long MILLISECOND = 1L;
    private static long SECOND = 1000L;
    private static long MINUTE;
    private static long HOUR;
    private static long DAY;
    private static long YEAR;

    private static final String[] PATTERN_LIST = {
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd",
            "yyyyMMdd HHmm",
            "yyyy.MM.ddHH:mmss",
            "yyyy.MM.ddHH:mm:ss",
            "yyyy.MM.ddHH:mm",
            "yyyy-MM-ddHH:mm:ss",
            "yyyy-MMddHH:mmss"
    };

    public DateTimeUtil() {
    }

    public static String getDateTimeString(Calendar c) {
        return getDateTimeString(c.getTime());
    }

    public static String getDateTimeString(Date date) {
        return getDateTimeString(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String getDateTimeString(Calendar c, String format) {
        return getDateTimeString(c.getTime(), format);
    }

    public static String getDateTimeString(Date date, String format) {
        return (new SimpleDateFormat(format)).format(date);
    }

    public static String getDateString(Calendar c) {
        return getDateString(c.getTime());
    }

    public static String getDateString(Date date) {
        return getDateString(date, "yyyy-MM-dd");
    }

    public static String getDateString(Calendar c, String format) {
        return getDateString(c.getTime(), format);
    }

    public static String getDateString(Date date, String format) {
        return (new SimpleDateFormat(format)).format(date);
    }

    public static Calendar getDateTime(String str) throws ParseException {
        return getDateTime(str, "yyyy-MM-dd HH:mm:ss");
    }

    public static Calendar getDateTime(String str, String format) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime((new SimpleDateFormat(format)).parse(str));
        return cal;
    }

    public static Calendar getDate(String str) throws ParseException {
        return getDate(str, "yyyy-MM-dd");
    }

    public static Calendar getDate(String str, String format) throws ParseException {
        Calendar cal = Calendar.getInstance();
        cal.setTime((new SimpleDateFormat(format)).parse(str));
        return cal;
    }

    public static long getTimeInterval(Date before, Date later) {
        return getTimeInterval(before, later, MILLISECOND);
    }

    public static long getTimeInterval(Date before, Date later, long unit) {
        return (later.getTime() - before.getTime()) / unit;
    }

    public static Calendar getNow() {
        return Calendar.getInstance();
    }

    public static Date getNowDate() {
        return Calendar.getInstance().getTime();
    }

    public static Date getFirstSecondOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal = getFirstSecondOfDay(cal);
        return cal.getTime();
    }

    public static Calendar getFirstSecondOfDay(Calendar cal) {
        cal.set(11, 0);
        cal.set(12, 0);
        cal.set(13, 0);
        cal.set(14, 0);
        return cal;
    }

    public static Date getLastSecondOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal = getLastSecondOfDay(cal);
        return cal.getTime();
    }

    public static Calendar getLastSecondOfDay(Calendar cal) {
        cal.set(11, 23);
        cal.set(12, 59);
        cal.set(13, 59);
        cal.set(14, 999);
        return cal;
    }

    public static Calendar getPreviousMonthFirstDay() {
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, -1);
        lastDate.set(5, 1);
        lastDate.set(11, 0);
        lastDate.set(12, 0);
        lastDate.set(13, 0);
        lastDate.set(14, 0);
        return lastDate;
    }

    public static Calendar getPreviousMonthLastDay() {
        Calendar lastDate = Calendar.getInstance();
        lastDate.add(2, -1);
        lastDate.set(5, 1);
        lastDate.roll(5, -1);
        lastDate.set(11, 23);
        lastDate.set(12, 59);
        lastDate.set(13, 59);
        lastDate.set(14, 999);
        return lastDate;
    }

    public static Calendar getCurrentWeekFirstDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(7, 2);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }

    public static Calendar getCurrentWeekLastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(7, 7);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        return calendar;
    }

    public static Calendar getCurrentMonthFirstDay() {
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(5, 1);
        lastDate.set(11, 0);
        lastDate.set(12, 0);
        lastDate.set(13, 0);
        lastDate.set(14, 0);
        return lastDate;
    }

    public static Calendar getCurrentMonthLastDay() {
        Calendar lastDate = Calendar.getInstance();
        lastDate.set(5, 1);
        lastDate.roll(5, -1);
        lastDate.set(11, 0);
        lastDate.set(12, 0);
        lastDate.set(13, 0);
        lastDate.set(14, 0);
        return lastDate;
    }

    public static Date getDateBefore(Date d, int unit, int value) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.add(unit, -value);
        return now.getTime();
    }

    public static Date getDateAfter(Date d, int unit, int value) {
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.add(unit, value);
        return now.getTime();
    }

    public static Timestamp getTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    public static int getAge(Date birthday, Date now) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        int yearNow = cal.get(1);
        int monthNow = cal.get(2);
        int dayOfMonthNow = cal.get(5);
        cal.setTime(birthday);
        int yearBirth = cal.get(1);
        int monthBirth = cal.get(2);
        int dayOfMonthBirth = cal.get(5);
        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    --age;
                }
            } else {
                --age;
            }
        }

        return age;
    }

    public static String dateStrFormat(String str) {
        Date date;
        try {
            date = DateUtils.parseDate(str, PATTERN_LIST);
            if (date != null) {
                return getDateTimeString(date);
            }
        } catch (ParseException e) {

        }
        return str;
    }

    public static Date parse(String str) {
        Date date = null;
        try {
            date = DateUtils.parseDate(str, PATTERN_LIST);
        } catch (ParseException e) {

        }
        return date;
    }

//    public static void main(String[] args) {
//        System.out.println(getDateAfter(getNowDate(), 12, 30));
//    }

    static {
        MINUTE = SECOND * 60L;
        HOUR = MINUTE * 60L;
        DAY = HOUR * 24L;
        YEAR = DAY * 365L;
    }

}
