package com.thc.platform.config;

import com.thc.platform.common.interceptor.GlobalAuthInterceptor;
import com.thc.platform.modules.wechat.constant.WeChatConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Autowired
	private RedisTemplate<String, String> redisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new GlobalAuthInterceptor(redisTemplate))
        	.addPathPatterns("/help/admin/**")
        	.addPathPatterns("/sms/**")
        	.excludePathPatterns("/sms/send/**")
        	.excludePathPatterns("/sms/send-record/**")
			.addPathPatterns(WeChatConstant.PATH_PREFIX + "/**")
        	.excludePathPatterns(WeChatConstant.PATH_PREFIX + "/mini/**")
        	.excludePathPatterns(WeChatConstant.PATH_PREFIX + "/public/**")
        	.excludePathPatterns(WeChatConstant.PATH_PREFIX + "/open/**")
        	.addPathPatterns("/notice/**");

    }

}
