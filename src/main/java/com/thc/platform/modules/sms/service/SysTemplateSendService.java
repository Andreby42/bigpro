package com.thc.platform.modules.sms.service;

import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.SerialNumUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.sms.dto.CCPRestInvokeInfo;
import com.thc.platform.modules.sms.dto.SmsSendIn;
import com.thc.platform.modules.sms.dto.SysTemplateSingleSendIn;
import com.thc.platform.modules.sms.entity.ReceiveRecordDataEntity;
import com.thc.platform.modules.sms.entity.ReceiveRecordEntity;
import com.thc.platform.modules.sms.entity.SendRecordEntity;
import com.thc.platform.modules.sms.entity.SmsSendNodeEntity;
import com.thc.platform.modules.sms.entity.SmsSendNodeTenantStatusEntity;
import com.thc.platform.modules.sms.entity.SysTemplateEntity;
import com.thc.platform.modules.sms.entity.SysTemplateTenantVerEntity;

/**
 * 系统模板发送服务
 */
@Service
public class SysTemplateSendService {

	private static final Logger logger = LoggerFactory.getLogger(SysTemplateSendService.class);
	
	@Autowired
	private ReceiveRecordService receiveRecordService;
	@Autowired
	private ReceiveRecordDataService receiveRecordDataService;
	@Autowired
	private SendRecordService sendRecordService;
	@Autowired
	private SysTemplateService sysTemplateService;
	@Autowired
	private SysTemplateTenantVerService sysTemplateTenantVerService;
	@Autowired
	private TenantYtxService tenantYtxService;
	@Autowired
	private SmsSendNodeService smsSendNodeService;
	@Autowired
	private SmsSendNodeTenantStatusService smsSendNodeTenantStatusService;
	
	/**
	 * 单条发送
	 */
	public void send(SysTemplateSingleSendIn in) {
		String receiveRecordId = receiveRecord(in);
		
		ReceiveRecordEntity updateCR = new ReceiveRecordEntity();
		updateCR.setId(receiveRecordId);
		
		String templateId = in.getTemplateId();
		
		// 模版不存在, 记录错误信息并返回
		SysTemplateEntity st = sysTemplateService.getByCode(templateId);
		if(st == null) {
			updateCR.setStatus(ReceiveRecordEntity.STATUS_FAIL);
			updateCR.setNotes("未找到相关系统模版。id: " + templateId);
			receiveRecordService.updateById(updateCR);
			return;
		}
		
		// 处理系统模版发送内容错误, 记录错误信息并返回
		String content = null;
		try {
			content = getSysTemplateSendContent(st.getContent(), in.getPayload(), templateId);
		} catch (Exception e) {
			logger.error("处理系统模版发送内容错误", e);
			updateCR.setStatus(SendRecordEntity.STATUS_FAIL);
			updateCR.setNotes("处理系统模版发送内容错误: " + e.getMessage());
			receiveRecordService.updateById(updateCR);
			return;
		}
		
		// 发送并记录相关信息
		SendRecordEntity sendRC = new SendRecordEntity();
		sendRC.setId(SerialNumUtil.serialNum());
		sendRC.setTenantId(in.getTenantId());
		sendRC.setReceiveRecordId(receiveRecordId);
		// 模版相关信息
		sendRC.setTemplateId(templateId);
		sendRC.setPayload(in.getPayload());
		// 机构科室
		loadOrjSubject(sendRC, in.getOrgSubject());
		// 发送方信息
		loadSender(sendRC, in.getSender());
		// 接收方信息
		loadReceiver(sendRC, in.getReceiver());
		// 短信相关信息
		sendRC.setType(st.getSmsType());
		sendRC.setSignTypeId(in.getSignTypeId());
		sendRC.setContent(content);
		sendRC.setFeeNum(0);// 后面增加计算
		sendRC.setCreateTime(new Date());
		
		String smsSendNodeCode = in.getSmsSendNodeCode();
		if(StringUtil.isNotEmpty(smsSendNodeCode)) {
			SmsSendNodeEntity smsSendNode = smsSendNodeService.getByCode(smsSendNodeCode);
			// 发送节点存在
			if(smsSendNode != null) {
				SmsSendNodeTenantStatusEntity smsSendNodeTenantStatus = 
						smsSendNodeTenantStatusService.getByTenantIdAndNodeId(in.getTenantId(), smsSendNode.getId());
				if(smsSendNodeTenantStatus != null 
						&& smsSendNodeTenantStatus.getStatus() == SmsSendNodeTenantStatusEntity.STATUS_OFF) {
					
					String msg = "租户发送节点关闭。smsSendNodeCode: " + smsSendNodeCode;
					sendRC.setNotes(msg);
					sendRC.setStatus(SendRecordEntity.STATUS_FAIL);
					sendRecordService.save(sendRC);
					updateCR.setStatus(ReceiveRecordEntity.STATUS_FAIL);
					updateCR.setNotes(msg);
					receiveRecordService.updateById(updateCR);
					return;
				}
			}
		}
			
		// 发送
		try {
			CCPRestInvokeInfo invokeInfo = tenantYtxService.sendSMS(
					in.getTenantId(), st.getSmsType(), in.getSignTypeId(), sendRC.getMobiles(), content, sendRC.getId());
			
			sendRC.setExternalResCode(invokeInfo.getResCode());
			sendRC.setExternalResData(invokeInfo.getResData());
			sendRC.setExternalTemplateId(invokeInfo.getTemplateId());
			sendRC.setNotes(invokeInfo.getNotes());
			
			if(invokeInfo.isSuccess()) {
				sendRC.setStatus(SendRecordEntity.STATUS_SUCCESS);
				sendRC.setFeeNum(invokeInfo.getFeeNum());
			} else {
				sendRC.setStatus(SendRecordEntity.STATUS_FAIL);
			}
		} catch (Exception e) {
			logger.error("发送错误", e);

			String errMsg = e.getMessage();
			
			sendRC.setStatus(SendRecordEntity.STATUS_FAIL);
			sendRC.setNotes(errMsg);
			
			updateCR.setStatus(ReceiveRecordEntity.STATUS_FAIL);
			updateCR.setNotes(errMsg);
		}
		sendRecordService.save(sendRC);
		
		updateCR.setStatus(ReceiveRecordEntity.STATUS_SUCCESS);
		receiveRecordService.updateById(updateCR);
	} 
	
	private void loadOrjSubject(SendRecordEntity record, SmsSendIn.OrgSubjectInfo os) {
		if(os == null)
			return;
		
		record.setOrgId(os.getOrgId());
		record.setOrgName(os.getOrgName());
		record.setSubjectId(os.getSubjectId());
		record.setSubjectName(os.getSubjectName());
	}
	
	private void loadSender(SendRecordEntity record, SmsSendIn.SenderInfo sender) {
		if(sender == null)
			return;
		
		record.setSenderType(sender.getType());
		record.setSenderId(sender.getId());
		record.setSenderName(sender.getName());
	}
	
	private void loadReceiver(SendRecordEntity record, SysTemplateSingleSendIn.ReceiverInfo receiver) {
		if(receiver == null)
			return;
		
		record.setReceiverType(receiver.getType());
		record.setReceiverId(receiver.getId());
		record.setReceiverName(receiver.getName());
		record.setMobiles(receiver.getMobile());
	}
	
	private String receiveRecord(SysTemplateSingleSendIn in) {
		ReceiveRecordEntity entity = new ReceiveRecordEntity();
		
		entity.setId(SerialNumUtil.serialNum());
		entity.setTenantId(in.getTenantId());
		entity.setAppCode(in.getAppCode());
		entity.setAppSerialNum(in.getAppSerialNum());
		entity.setStatus(ReceiveRecordEntity.STATUS_RECEIVED);
		entity.setCreateTime(new Date());
		
		receiveRecordService.save(entity);
		
		ReceiveRecordDataEntity dataEntity = new ReceiveRecordDataEntity();
		dataEntity.setId(entity.getId());
		dataEntity.setTenantId(entity.getTenantId());
		dataEntity.setReqData(JSON.toJSONString(in));
		dataEntity.setCreateTime(entity.getCreateTime());
		
		receiveRecordDataService.save(dataEntity);
		
		return entity.getId();
	}
	
	private String getSysTemplateSendContent(String content, String payload, String templateId) {
		SysTemplateTenantVerEntity sttv = sysTemplateTenantVerService.getById(templateId);
		if(sttv != null)
			content = sttv.getContent();
		
		Map<String, Object> payloadObj = JSON.parseObject(payload);
		for(Map.Entry<String, Object> entry : payloadObj.entrySet()) {
			if(entry.getValue() == null)
				continue;
			
			String originalValue = "\\{" + entry.getKey() + "\\}";
			String replaceValue = entry.getValue().toString();
			content = content.replaceAll(originalValue, replaceValue);
		}
		int startIndex = content.indexOf("{");
		int endIndex = content.indexOf("}");
		
		if(startIndex >= 0 && endIndex > 0 && startIndex > endIndex)
			throw BEUtil.failNormal("参数不全");
		
		return content;
	}

}
