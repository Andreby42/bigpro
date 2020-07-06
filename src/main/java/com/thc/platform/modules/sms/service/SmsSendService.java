package com.thc.platform.modules.sms.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gexin.fastjson.JSON;
import com.thc.platform.modules.sms.dto.ManualBatchSendIn;
import com.thc.platform.modules.sms.dto.ManualCcSendIn;
import com.thc.platform.modules.sms.dto.SmsSendIn;
import com.thc.platform.modules.sms.dto.SysTemplateSingleSendIn;

/**
 * 短信发送服务
 */
@Service
public class SmsSendService {

	private Logger logger = LoggerFactory.getLogger(SmsSendService.class);
	
	@Autowired
	private SysTemplateSendService sysTemplateSendService;
	@Autowired
	private ManualSendService manualSendService;
	@Autowired
	private ReceiveRecordService receiveRecordService;
	
	public void send(SmsSendIn in) {
		if(in == null)
			return;

		logger.info("consume msg: " + JSON.toJSONString(in));
		
		String appCode = in.getAppCode();
		String appSerialNum = in.getAppSerialNum();
		Integer tenantId = in.getTenantId();
		if(receiveRecordService.countByBusiId(tenantId, appCode, appSerialNum) > 0) {
			logger.info("重复短信：" + JSON.toJSONString(in));
			return;
		}
			
		try {
			if(in instanceof SysTemplateSingleSendIn)
				sysTemplateSendService.send((SysTemplateSingleSendIn)in);
			else if(in instanceof ManualBatchSendIn)
				manualSendService.batchSend((ManualBatchSendIn)in);
			else if(in instanceof ManualCcSendIn)
				manualSendService.ccSend((ManualCcSendIn)in);
		} catch (Exception e) {
			logger.error("process sms data error. data: " + JSON.toJSONString(in), e);
		}
		
	}
	
}
