package com.thc.platform.modules.sms.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.util.SerialNumUtil;
import com.thc.platform.modules.sms.constant.SmsType;
import com.thc.platform.modules.sms.dto.CCPRestInvokeInfo;
import com.thc.platform.modules.sms.dto.ManualBatchSendIn;
import com.thc.platform.modules.sms.dto.ManualCcSendIn;
import com.thc.platform.modules.sms.dto.SmsSendIn;
import com.thc.platform.modules.sms.entity.ReceiveRecordDataEntity;
import com.thc.platform.modules.sms.entity.ReceiveRecordEntity;
import com.thc.platform.modules.sms.entity.SendRecordEntity;

@Service
public class ManualSendService {

	private static final Logger logger = LoggerFactory.getLogger(ManualSendService.class);
	
	@Autowired
	private ReceiveRecordService receiveRecordService;
	@Autowired
	private ReceiveRecordDataService receiveRecordDataService;
	@Autowired
	private SendRecordService sendRecordService;
	@Autowired
	private TenantYtxService tenantYtxService;
	
	public void batchSend(ManualBatchSendIn in) {
		Date currentTime = new Date();
		
		String receiveRecordId = receiveRecord(in, currentTime);
		
		ReceiveRecordEntity updateCR = new ReceiveRecordEntity();
		updateCR.setId(receiveRecordId);
		
		// 发送并记录相关信息
		SendRecordEntity sendRC = new SendRecordEntity();
		sendRC.setTenantId(in.getTenantId());
		sendRC.setReceiveRecordId(receiveRecordId);
		// 机构科室
		loadOrgSubject(sendRC, in.getOrgSubject());
		// 发送方信息
		loadSender(sendRC, in.getSender());
		
		// 短信相关信息
		sendRC.setType(SmsType.NOTICE);
		sendRC.setSignTypeId(in.getSignTypeId());
		sendRC.setCreateTime(currentTime);
		
		for(ManualBatchSendIn.ReceiverInfo receiver : in.getReceivers()) {
			sendRC.setId(SerialNumUtil.serialNum(currentTime));
			// 接收方信息
			loadReceiver(sendRC, receiver);
			sendRC.setFeeNum(0);// 后面增加计算
			
			// 发送
			try {
				CCPRestInvokeInfo invokeInfo = tenantYtxService.sendSMS(
						in.getTenantId(), SmsType.NOTICE, in.getSignTypeId(), sendRC.getMobiles(), sendRC.getContent(), sendRC.getId());
				
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
		}
		// 保存接收记录信息
		updateCR.setStatus(ReceiveRecordEntity.STATUS_SUCCESS);
		receiveRecordService.updateById(updateCR);
	} 
	
	private void loadOrgSubject(SendRecordEntity record, SmsSendIn.OrgSubjectInfo os) {
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
	
	private void loadReceiver(SendRecordEntity record, ManualBatchSendIn.ReceiverInfo receiver) {
		if(receiver == null)
			return;
		
		record.setReceiverType(receiver.getType());
		record.setReceiverId(receiver.getId());
		record.setReceiverName(receiver.getName());
		record.setMobiles(receiver.getMobile());
		record.setContent(receiver.getContent());
	}
	
	private String receiveRecord(SmsSendIn in, Date currentTime) {
		ReceiveRecordEntity entity = new ReceiveRecordEntity();
		
		entity.setId(SerialNumUtil.serialNum(currentTime));
		entity.setTenantId(in.getTenantId());
		entity.setAppCode(in.getAppCode());
		entity.setAppSerialNum(in.getAppSerialNum());
		entity.setStatus(ReceiveRecordEntity.STATUS_RECEIVED);
		entity.setCreateTime(currentTime);
		
		receiveRecordService.save(entity);
		
		ReceiveRecordDataEntity dataEntity = new ReceiveRecordDataEntity();
		dataEntity.setId(entity.getId());
		dataEntity.setTenantId(entity.getTenantId());
		dataEntity.setReqData(JSON.toJSONString(in));
		dataEntity.setCreateTime(entity.getCreateTime());
		
		receiveRecordDataService.save(dataEntity);
		return entity.getId();
	}
	
	public void ccSend(ManualCcSendIn in) {
		Date currentTime = new Date();
		
		String receiveRecordId = receiveRecord(in, currentTime);
		
		ReceiveRecordEntity updateCR = new ReceiveRecordEntity();
		updateCR.setId(receiveRecordId);
		
		// 发送并记录相关信息
		SendRecordEntity sendRC = new SendRecordEntity();
		sendRC.setId(SerialNumUtil.serialNum(currentTime));
		sendRC.setTenantId(in.getTenantId());
		sendRC.setReceiveRecordId(receiveRecordId);
		// 机构科室
		loadOrgSubject(sendRC, in.getOrgSubject());
		// 发送方信息
		loadSender(sendRC, in.getSender());
		// 接收方信息
		loadReceiver(sendRC, in.getReceiver());
		// 短信相关信息
		sendRC.setType(SmsType.NOTICE);
		sendRC.setSignTypeId(in.getSignTypeId());
		sendRC.setFeeNum(0);// 后面增加计算
		sendRC.setCreateTime(currentTime);
		
		// 发送
		try {
			CCPRestInvokeInfo invokeInfo = tenantYtxService.sendSMS(
					in.getTenantId(), SmsType.NOTICE, in.getSignTypeId(), sendRC.getMobiles(), sendRC.getContent(), sendRC.getId());
			
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
		// 保存接收记录信息
		updateCR.setStatus(ReceiveRecordEntity.STATUS_SUCCESS);
		receiveRecordService.updateById(updateCR);
	}
	
	private void loadReceiver(SendRecordEntity record, ManualCcSendIn.ReceiverInfo receiver) {
		if(receiver == null)
			return;
		
		record.setReceiverType(receiver.getType());
		record.setReceiverId(receiver.getId());
		record.setReceiverName(receiver.getName());
		record.setContent(receiver.getContent());
		
		StringBuilder sb = new StringBuilder();
		int count = 0;
		for(String mobile : receiver.getMobiles()) {
			if(count > 0)
				sb.append(",");
			
			sb.append(mobile);
			count ++;
		}
		
		record.setMobiles(sb.toString());
	}
}
