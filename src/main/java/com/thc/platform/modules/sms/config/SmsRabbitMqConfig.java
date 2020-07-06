package com.thc.platform.modules.sms.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thc.platform.modules.sms.dto.SmsSendIn;
import com.thc.platform.modules.sms.service.SmsSendService;

/**
 * 短信 MQ 配置
 */
@Configuration
public class SmsRabbitMqConfig {

	public static final String EXCHANGE_NAME = "exchange.global-platform-extend.sms";
	public static final String ROUTING_KEY = "sms";
	private static final String QUEUE = "queue.global-platform-extend.sms";
	
	@Autowired
	private SmsSendService smsSendService;
	
    @Bean("smsExchange")
    public DirectExchange smsExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean("smsQueue")
    public Queue smsSysTemplateQueue() {
        return new Queue(QUEUE, true);
    }
    
    @Bean
    public Binding binding(@Qualifier("smsQueue") Queue smsQueue
    		, @Qualifier("smsExchange") DirectExchange smsExchange) {
        return BindingBuilder.bind(smsQueue).to(smsExchange).with(ROUTING_KEY);
    }
    
	@RabbitListener(queues = QUEUE)
	public void processSysTemplateSms(SmsSendIn in) { 
		smsSendService.send(in);
	}
	
}
