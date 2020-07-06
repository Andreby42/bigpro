package com.thc.platform.common.util;

public class StringUtil {

	public static boolean isEmpty(String value) {
		return value == null || "".equals(value.trim());
	}
	
	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}
	
}
