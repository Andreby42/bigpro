package com.thc.platform.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

public class SerialNumUtil {

	public static String serialNum() {
		Date currentTime = new Date();
		return serialNum(currentTime);
		
	}
	
	public static String serialNum(Date currentTime) {
		DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String prefix = df.format(currentTime);
		
		ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
		
		String rNum = String.valueOf(threadLocalRandom.nextInt(10000));
		
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		
		if(rNum.length() == 1)
			sb.append("000");
		else if(rNum.length() == 2)
			sb.append("00");
		else if(rNum.length() == 3)
			sb.append("0");
		
		sb.append(rNum);
		
		
		return sb.toString();
		
	}
	
}
