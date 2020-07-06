package com.thc.platform.modules.sms.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.thc.platform.modules.sms.config.SmsRabbitMqConfig;

@Component
public class SmsRabbitMqService {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	
	public <T> void sendRabbitMq(String routingKey, T data) {
		rabbitTemplate.convertAndSend(SmsRabbitMqConfig.EXCHANGE_NAME, routingKey, data);
	}
	
}
