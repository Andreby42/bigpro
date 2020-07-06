package com.thc.platform.common.interceptor;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.common.util.TokenUtil;

public class GlobalAuthInterceptor extends HandlerInterceptorAdapter {

//	private static Logger logger = LoggerFactory.getLogger(GlobalAuthInterceptor.class);
	
	private static final String UNANTHORIZED_RESULT = Api.UNANTHORIZED_RESULT.toString();
	
	private RedisTemplate<String, String> redisTemplate;
	
	public GlobalAuthInterceptor(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		String encodedToken = request.getHeader("x-access-token");
		// 未携带token
		if(StringUtil.isEmpty(encodedToken)) {
			write(response, UNANTHORIZED_RESULT);
			return false;
		}
		
		// 验证 redis 缓存的token
		String tokenKey = getTokenKey(encodedToken);
		String cachedToken = redisTemplate.opsForValue().get(tokenKey);
		if(cachedToken == null) {
			write(response, UNANTHORIZED_RESULT);
			return false;
		}
		// 更新 token 过期时间
		redisTemplate.expire(tokenKey, 2, TimeUnit.HOURS);
		TokenUtil.setToken(encodedToken);
		return true;
	}
	
	private void write(HttpServletResponse response, String text) throws IOException {
		response.setHeader("Content-type", "application/json;charset=UTF-8");
		response.getWriter().write(text);
	}

	public String getTokenKey(String token) {
		return "global-platform:token:thc:" + token;
	}
}
