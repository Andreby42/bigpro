package com.thc.platform.modules.sms.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gexin.fastjson.JSON;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.sms.config.SmsRabbitMqConfig;
import com.thc.platform.modules.sms.dto.SysTemplateSingleSendIn;
import com.thc.platform.modules.sms.service.SmsRabbitMqService;

/**
 * 短信发送接口类
 */
@RestController
@RequestMapping("/sms/send/sys-template")
public class SysTemplateSendController {

	private Logger logger = LoggerFactory.getLogger(SysTemplateSendController.class);
	
	@Autowired
	private SmsRabbitMqService rabbitMqService;
	
	@PostMapping("/single")
	public Api<Object> sysTemplateSend(@RequestBody SysTemplateSingleSendIn in) {
		in.validate();
		
		logger.info("receive msg: " + JSON.toJSONString(in));
		
		rabbitMqService.sendRabbitMq(SmsRabbitMqConfig.ROUTING_KEY, in);
		return Api.SUCCESS_RESULT;
	}
	
}
