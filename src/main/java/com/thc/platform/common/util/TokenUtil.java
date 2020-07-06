package com.thc.platform.common.util;

public class TokenUtil {

	private static ThreadLocal<String> tl = new ThreadLocal<>(); 
	
	public static void setToken(String token) {
		tl.set(token);
	}
	
	public static String getToken() {
		return tl.get();
	}
}
