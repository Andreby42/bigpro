package com.thc.platform.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	/** 时间格式(yyyy-MM-dd HH:mm:ss) */
	public final static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	public static String formatDefaultPattern(Date date) {
		DateFormat df = new SimpleDateFormat(DATE_TIME_PATTERN);
		return df.format(date);
	}
	
	public static Date parseDefaultPattern(String value) {
		DateFormat df = new SimpleDateFormat(DATE_TIME_PATTERN);
		try {
			return df.parse(value);
		} catch (Exception e) {
			return null;
		}
	}
	
}
