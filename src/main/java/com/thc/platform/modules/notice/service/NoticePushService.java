package com.thc.platform.modules.notice.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.SerialNumUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.notice.config.NoticeRabbitMqConfig;
import com.thc.platform.modules.notice.dto.NoticePushIn;
import com.thc.platform.modules.notice.entity.NoticeReceiveRecordEntity;
import com.thc.platform.modules.notice.entity.NoticeRecordEntity;
import com.thc.platform.modules.notice.entity.NoticeTemplateEntity;

@Service
public class NoticePushService {

	private final Logger logger = LoggerFactory.getLogger(NoticePushService.class);
	
	public static final int NOTICE_ACTION_ADD = 1;
	public static final int NOTICE_ACTION_RECALL = 2;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private NoticeTemplateService noticeTemplateService;
	@Autowired
	private NoticeReceiveRecordService noticeReceiveRecordService;
	@Autowired
	private NoticeRecordService noticeRecordService;
	
	public void receiveAndLog(NoticePushIn in) {
		Date currentTime = new Date();
		NoticeReceiveRecordEntity recordEntity = new NoticeReceiveRecordEntity();
		recordEntity.setId(SerialNumUtil.serialNum());
		recordEntity.setAppCode(in.getAppCode());
		recordEntity.setAppSerialNum(in.getAppSerialNum());
		recordEntity.setReqData(JSON.toJSONString(in));
		recordEntity.setCreateTime(currentTime);
		
		try {
			receive(in, currentTime);
		} catch (Exception e) {
			logger.error("process notice error.", e);
			recordEntity.setNotes(e.getMessage());
		}
		noticeReceiveRecordService.save(recordEntity);
	}

	private void receive(NoticePushIn in, Date currentTime) {
		String content = getContent(in.getTemplateCode(), in.getPayload());

		StringBuilder sb = new StringBuilder();
		sb.append(content);
		sb.setLength(128);
		String title = sb.toString();
		
		NoticeRecordEntity record = new NoticeRecordEntity();
		
		record.setAppCode(in.getAppCode());
		record.setAppSerialNum(in.getAppSerialNum());
		record.setTitle(title);
		record.setStatus(NoticeRecordEntity.STATUS_UNREAD);
		record.setTemplateCode(in.getTemplateCode());
		record.setContent(content);
		record.setConcurrent(in.getConcurrent());
		record.setExtend1(in.getExtend1());
		record.setExtend2(in.getExtend2());
		record.setExtend3(in.getExtend3());
		record.setExtend4(in.getExtend4());
		record.setExtend5(in.getExtend5());
		record.setUpdateTime(currentTime);
		record.setCreateTime(currentTime);
		
		String receiverIds = in.getReceiverIds();
		String sendContent = getNticeAddPushContent(in.getTemplateCode());
		for(String receiverId : receiverIds.split(",")) {
        	if(StringUtil.isEmpty(receiverId))
        		continue;
        	
        	record.setId(SerialNumUtil.serialNum());
        	record.setReceiverId(receiverId);
        	
    		try {
    			pushMsgTip(sendContent, receiverId);
				record.setPushMqStatus(NoticeRecordEntity.PUSH_MQ_STATUS_SUCCESS);
			} catch (AmqpException e) {
				logger.error("推送mq失败", e);
				record.setPushMqStatus(NoticeRecordEntity.PUSH_MQ_STATUS_FAIL);
				record.setNotes("推送mq失败：" + e.getMessage());
			}
    		noticeRecordService.save(record);
		}
	}
	
	private String getContent(String code, String payload) {
		NoticeTemplateEntity template = noticeTemplateService.getByCode(code);
		if (template == null)
			throw BEUtil.failNormal("模版不存在。code: " + code);

		String content = template.getContent();

		Map<String, Object> payloadObj = JSON.parseObject(payload);
		for (Map.Entry<String, Object> entry : payloadObj.entrySet()) {
			if (entry.getValue() == null)
				continue;

			String originalValue = "\\{" + entry.getKey() + "\\}";
			String replaceValue = entry.getValue().toString();
			content = content.replaceAll(originalValue, replaceValue);
		}
		int startIndex = content.indexOf("{");
		int endIndex = content.indexOf("}");

		if (startIndex >= 0 && endIndex > 0 && startIndex > endIndex)
			throw BEUtil.failNormal("参数不全");

		return content;
	}
	
	private String getNticeAddPushContent(String templateCode) {
		Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("notice", true);
        contentMap.put("action", NOTICE_ACTION_ADD);
        contentMap.put("templateCode", templateCode);
        contentMap.put("templateContent", "");
        
        return JSON.toJSONString(contentMap);
	}
	
	public String getNticeRecallPushContent(String templateCode) {
		Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("notice", true);
        contentMap.put("action", NOTICE_ACTION_RECALL);
        contentMap.put("templateCode", templateCode);
        contentMap.put("templateContent", "");
        
        return JSON.toJSONString(contentMap);
	}
	
	public void pushMsgTip(String data, String routingKey) {
		rabbitTemplate.convertAndSend(NoticeRabbitMqConfig.EXCHANGE_NAME_MSG_TIP, routingKey, data);
	}
}
