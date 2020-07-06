package com.thc.platform.modules.tencentcloud.service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import sun.misc.BASE64Encoder;

@Service
public class TencentCloudService {

	private static final String TENCENT_CLOUD_SIGNATURE_KEY = "global-platform-extend.tencentcloud.uploadSignature";
	
	private Random random = new Random();
	private Logger logger = LoggerFactory.getLogger(TencentCloudService.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	public String getSignature(String classId) {
		long currentTime = System.currentTimeMillis() / 1000;
		int randomNum = random.nextInt(java.lang.Integer.MAX_VALUE);
		int signValidDuration = 3600;
		
		try {
			String signature = redisTemplate.opsForValue().get(TENCENT_CLOUD_SIGNATURE_KEY + "." + classId);
			if(StringUtil.isEmpty(signature)) {
				signature = getUploadSignature(currentTime, randomNum, classId, signValidDuration);
				redisTemplate.opsForValue().set(TENCENT_CLOUD_SIGNATURE_KEY, signature, 3000, TimeUnit.SECONDS);
			}
			return signature;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw BEUtil.failNormal("获取签名失败，请联系管理员." + e.getMessage());
		}
		
	}
	
	private String getUploadSignature(long currentTime, int randomNum, String classId, int signValidDuration) throws Exception {
		String strSign = "";
		String contextStr = "";

		long endTime = (currentTime + signValidDuration);
		contextStr += "secretId=" + java.net.URLEncoder.encode(TencentCloudConfig.SECRET_ID, "utf8");
		contextStr += "&currentTimeStamp=" + currentTime;
		contextStr += "&expireTime=" + endTime;
		contextStr += "&random=" + randomNum;
		if(StringUtil.isNotEmpty(classId))
			contextStr += "&classId=" + classId;

		try {
			Mac mac = Mac.getInstance(TencentCloudConfig.HMAC_ALGORITHM);
			SecretKeySpec secretKeySpec = new SecretKeySpec(TencentCloudConfig.SECRET_KEY.getBytes(TencentCloudConfig.CONTENT_CHARSET), mac.getAlgorithm());
			mac.init(secretKeySpec);

			byte[] hash = mac.doFinal(contextStr.getBytes(TencentCloudConfig.CONTENT_CHARSET));
			byte[] sigBuf = byteMerger(hash, contextStr.getBytes("utf8"));
			strSign = base64Encode(sigBuf);
			strSign = strSign.replace(" ", "").replace("\n", "").replace("\r", "");
		} catch (Exception e) {
			throw e;
		}
		return strSign;
	}

	private byte[] byteMerger(byte[] byte1, byte[] byte2) {
		byte[] byte3 = new byte[byte1.length + byte2.length];
		System.arraycopy(byte1, 0, byte3, 0, byte1.length);
		System.arraycopy(byte2, 0, byte3, byte1.length, byte2.length);
		return byte3;
	}
	
	private String base64Encode(byte[] buffer) {
		BASE64Encoder encoder = new BASE64Encoder();
		return encoder.encode(buffer);
	}
	
	private class TencentCloudConfig {
		private static final String SECRET_ID = "AKIDCHGCGKik6M7j5BnF3XlvyXxBKI7H3tJY";
		private static final String SECRET_KEY = "J9m9RaVF4iPvM7vd8dy2soF2byRvDG8o";
		
		private static final String HMAC_ALGORITHM = "HmacSHA1";
		private static final String CONTENT_CHARSET = "UTF-8";
	}
	
}
