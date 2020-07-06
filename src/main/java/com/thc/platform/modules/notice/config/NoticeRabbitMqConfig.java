package com.thc.platform.modules.notice.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thc.platform.modules.notice.dto.NoticePushIn;
import com.thc.platform.modules.notice.service.NoticePushService;


@Configuration
public class NoticeRabbitMqConfig {

	public static final String EXCHANGE_NAME_RECEIVE = "exchange.direct.platform-extend.notice.receive";
	public static final String ROUTING_KEY = "notice";
	public static final String QUEUE = "queue.direct.platform-extend.notice.receive";
	public static final String EXCHANGE_NAME_MSG_TIP = "exchange.direct.notices.messageTip";
	
	private final Logger logger = LoggerFactory.getLogger(NoticeRabbitMqConfig.class);
	
	@Autowired
	private NoticePushService noticePushService;
	
    @Bean("noticeReceiveExchange")
    public DirectExchange staffTipExchange() {
        return new DirectExchange(EXCHANGE_NAME_RECEIVE);
    }
    
    @Bean("noticeReceiveQueue")
    public Queue noticeReceiveQueue() {
        return new Queue(QUEUE, true);
    }
    
    @Bean
    public Binding bindingNoticeReceive(@Qualifier("noticeReceiveQueue") Queue noticeReceiveQueue
    		, @Qualifier("noticeReceiveExchange") DirectExchange noticeReceiveExchange) {
        return BindingBuilder.bind(noticeReceiveQueue).to(noticeReceiveExchange).with(ROUTING_KEY);
    }
    
    @RabbitListener(queues = QUEUE)
	public void processMsg(NoticePushIn in) {
    	try {
    		noticePushService.receiveAndLog(in);
		} catch (Exception e) {
			logger.error("consume notice fail. appCode: " + in.getAppCode()
					+ "; appSerialNum: " + in.getAppSerialNum() 
					+ "; receiverIds: " + in.getReceiverIds()
				, e);
		}
    	
    	logger.info("consume notice success. appCode: {}; appSerialNum: {}; receiverIds: {}"
				, new Object[]{in.getAppCode(), in.getAppSerialNum(), in.getReceiverIds()});
    }

    @Bean
    public TopicExchange messageTipExchange() {
        return new TopicExchange(EXCHANGE_NAME_MSG_TIP);
    }
}
