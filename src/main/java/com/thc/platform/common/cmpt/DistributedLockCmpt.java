package com.thc.platform.common.cmpt;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class DistributedLockCmpt {

	private Logger logger = LoggerFactory.getLogger(DistributedLockCmpt.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	public boolean lock(String key, long lockTimeout, TimeUnit lockTimeoutUnit, int retryTimes, long retryInterval, TimeUnit retryIntervalUnit) {
		boolean locked = redisTemplate.opsForValue().setIfAbsent(key, "lock", lockTimeout, lockTimeoutUnit);
		if(locked)
			return locked;
		
		for(int i = 0; i < retryTimes; i ++) {
			try {
				retryIntervalUnit.sleep(retryInterval);
			} catch (InterruptedException e) {
				logger.error("thread sleep error.", e);
				Thread.currentThread().interrupt();
			}
			locked = redisTemplate.opsForValue().setIfAbsent(key, "lock", lockTimeout, lockTimeoutUnit);
			if(locked)
				break;
		}
		
		return locked;
	}
	
	public boolean release(String key) {
		return redisTemplate.delete(key);
	}
	
}
