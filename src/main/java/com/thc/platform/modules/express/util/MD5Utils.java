package com.thc.platform.modules.express.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thc.platform.common.util.BEUtil;

/**
 * md5加密
 */
public class MD5Utils {
	
	private static Logger logger = LoggerFactory.getLogger(MD5Utils.class);
	
	private static MessageDigest mdigest = null;
	private static char digits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
	
	private static MessageDigest getMdInst() {
		if (null == mdigest) {
			try {
				mdigest = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				logger.error("init error.", e);
			}
		}
		return mdigest;
	}

	public static String encode(String s) {
		if(null == s) {
			return "";
		}
		
		try {
			MessageDigest inst = getMdInst();
			if(inst == null)
				throw BEUtil.failNormal("init MessageDigest error.");
			
			byte[] bytes = s.getBytes();
			inst.update(bytes);
			
			byte[] md = inst.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = digits[byte0 >>> 4 & 0xf];
				str[k++] = digits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			logger.error("md5 encode error.", e);
			return null;
		}
	}
}