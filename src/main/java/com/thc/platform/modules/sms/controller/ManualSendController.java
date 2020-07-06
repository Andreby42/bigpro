package com.thc.platform.modules.sms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.sms.config.SmsRabbitMqConfig;
import com.thc.platform.modules.sms.dto.ManualBatchSendIn;
import com.thc.platform.modules.sms.dto.ManualCcSendIn;
import com.thc.platform.modules.sms.service.SmsRabbitMqService;

/**
 * 短信人工发送接口
 */
@RestController
@RequestMapping("/sms/send/manual")
public class ManualSendController {

	@Autowired
	private SmsRabbitMqService smsRabbitMqService;
	
	/**
	 * 批量发送
	 */
	@PostMapping("/batch")
	public Api<Object> batchSend(@RequestBody ManualBatchSendIn in) {
		in.validate();
		
		smsRabbitMqService.sendRabbitMq(SmsRabbitMqConfig.ROUTING_KEY, in);
		
		return Api.SUCCESS_RESULT;
	}
	
	/**
	 * cc发送
	 */
	@PostMapping("/cc")
	public Api<Object> ccSend(@RequestBody ManualCcSendIn in) {
		in.validate();
		
		smsRabbitMqService.sendRabbitMq(SmsRabbitMqConfig.ROUTING_KEY, in);
		
		return Api.SUCCESS_RESULT;
	}
	
}
