package com.thc.platform.modules.sms.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.sms.dao.SendRecordDao;
import com.thc.platform.modules.sms.dto.SendRecordComplexQryIn;
import com.thc.platform.modules.sms.dto.SmsRecordDetailOut;
import com.thc.platform.modules.sms.dto.SmsRecordDetailQryIn;
import com.thc.platform.modules.sms.dto.TenantOrgDaySendDto;
import com.thc.platform.modules.sms.entity.SendRecordEntity;

@Service
public class SendRecordService extends ServiceImpl<SendRecordDao, SendRecordEntity> {

	public PageOut<SendRecordEntity> complexQry(SendRecordComplexQryIn in) {
		LambdaQueryWrapper<SendRecordEntity> wrapper = Wrappers.lambdaQuery();
		
		if(in.getTenantId() != null)
			wrapper.eq(SendRecordEntity::getTenantId, in.getTenantId());
		
		if(StringUtil.isNotEmpty(in.getMobile()))
			wrapper.like(SendRecordEntity::getMobiles, in.getMobile());
		
		if(in.getType() != null)
			wrapper.eq(SendRecordEntity::getType, in.getType());
		
		if(in.getStatus() != null)
			wrapper.eq(SendRecordEntity::getStatus, in.getStatus());
		
		if(in.getSignTypeId() != null)
			wrapper.eq(SendRecordEntity::getSignTypeId, in.getSignTypeId());
		
		if(StringUtil.isNotEmpty(in.getTemplateId()))
			wrapper.eq(SendRecordEntity::getTemplateId, in.getTemplateId());
		
		if(in.getStartTime() != null)
			wrapper.ge(SendRecordEntity::getCreateTime, in.getStartTime());
		
		if(in.getEndTime() != null)
			wrapper.le(SendRecordEntity::getCreateTime, in.getEndTime());
			
		// 机构科室信息
		SendRecordComplexQryIn.OrgSubject orgSubject = in.getOrgSubject();
		if(orgSubject != null) {
			if(StringUtil.isNotEmpty(orgSubject.getOrgId()))
				wrapper.eq(SendRecordEntity::getOrgId, orgSubject.getOrgId());
			
			if(StringUtil.isNotEmpty(orgSubject.getOrgName()))
				wrapper.like(SendRecordEntity::getOrgName, orgSubject.getOrgName());
			
			if(StringUtil.isNotEmpty(orgSubject.getSubjectId()))
				wrapper.eq(SendRecordEntity::getSubjectId, orgSubject.getSubjectId());
			
			if(StringUtil.isNotEmpty(orgSubject.getSubjectName()))
				wrapper.like(SendRecordEntity::getSubjectName, orgSubject.getSubjectName());
		}
		// 发送方信息
		SendRecordComplexQryIn.Receiver receiver = in.getReceiver();
		if(receiver != null) {
			if(receiver.getReceiverType() != null) 
				wrapper.eq(SendRecordEntity::getReceiverType, receiver.getReceiverType());
			
			if(StringUtil.isNotEmpty(receiver.getReceiverId()))
				wrapper.eq(SendRecordEntity::getReceiverId, receiver.getReceiverId());
			
			if(StringUtil.isNotEmpty(receiver.getReceiverName()))
				wrapper.like(SendRecordEntity::getReceiverName, receiver.getReceiverName());
			
		}
		
		// 接收方信息
		SendRecordComplexQryIn.Sender sender = in.getSender();
		if(sender != null) {
			if(sender.getSenderType() != null) 
				wrapper.eq(SendRecordEntity::getSenderType, sender.getSenderType());
			
			if(StringUtil.isNotEmpty(sender.getSenderId()))
				wrapper.eq(SendRecordEntity::getSenderId, sender.getSenderId());
			
			if(StringUtil.isNotEmpty(sender.getSenderName()))
				wrapper.like(SendRecordEntity::getSenderName, sender.getSenderName());
		}
		
		Integer totalCount = baseMapper.selectCount(wrapper);
		
		wrapper.orderByDesc(SendRecordEntity::getCreateTime)
			.last("limit " + in.getOffset() + ", " + in.getPagesize());
		
		List<SendRecordEntity> items = baseMapper.selectList(wrapper);
		
		return new PageOut<SendRecordEntity>(items, totalCount);
	}
	
	public SendRecordEntity getByReceiveRecordId(String receiveRecordId) {
		return baseMapper.selectOne(
				Wrappers.<SendRecordEntity>lambdaQuery()
					.eq(SendRecordEntity::getReceiveRecordId, receiveRecordId)
			);
	}
	
	public List<Integer> getDisTenantIdByDay(String stDay) {
        return baseMapper.getDisTenantIdByDay(stDay);
    }
	
	public List<TenantOrgDaySendDto> stTenantOrgDaySend(Integer tenantId, String stDay) {
		return baseMapper.stTenantOrgDaySend(tenantId, stDay);
	}
	
	public PageOut<SmsRecordDetailOut> detailComplexQry(SmsRecordDetailQryIn in) {
		List<Map<String, Object>> items = baseMapper.complexQry(in.getTenantId()
				, in.getAppCode(), in.getAppSerialNum(), in.getMobile(), in.getOffset(), in.getPagesize());
		
		Integer totalCount = baseMapper.complexQryCount(
				in.getTenantId(), in.getAppCode(), in.getAppSerialNum(), in.getMobile());
		
		List<SmsRecordDetailOut> outs = new ArrayList<>(items.size());
		for(Map<String, Object> item : items) {
			SmsRecordDetailOut.ReceiveRecordOut rOut = new SmsRecordDetailOut.ReceiveRecordOut();
			rOut.setStatus((Integer)item.get("rStatus"));
			rOut.setReqData((String)item.get("rReqData"));
			rOut.setNotes((String)item.get("rNotes"));
			rOut.setCreateTime((Date)item.get("rCreateTime"));
			
			SmsRecordDetailOut.SendRecordOut sOut = new SmsRecordDetailOut.SendRecordOut();
			sOut.setTemplateId((String)item.get("sTemplateId"));
			sOut.setPayload((String)item.get("sPayload"));
			
			sOut.setOrgId((String)item.get("sOrgId"));
			sOut.setOrgName((String)item.get("sOrgName"));
			sOut.setSubjectId((String)item.get("sSubjectId"));
			sOut.setSubjectName((String)item.get("sSubjectName"));
			
			sOut.setSenderType((Integer)item.get("sSenderType"));
			sOut.setSenderId((String)item.get("sSenderId"));
			sOut.setSenderName((String)item.get("sSenderName"));
			
			sOut.setReceiverType((Integer)item.get("sReceiverType"));
			sOut.setReceiverId((String)item.get("sReceiverId"));
			sOut.setReceiverName((String)item.get("sReceiverName"));
			
			sOut.setType((Integer)item.get("sType"));
			sOut.setSignTypeId((String)item.get("sSignTypeId"));
			sOut.setContent((String)item.get("sContent"));
			sOut.setStatus((Integer)item.get("sStatus"));
			sOut.setFeeNum((Integer)item.get("sFeeNum"));
			
			sOut.setExternalTemplateId((String)item.get("sExternalTemplateId"));
			sOut.setExternalResData((String)item.get("sExternalResData"));
			sOut.setExternalResCode((String)item.get("sExternalResCode"));
			
			sOut.setNotes((String)item.get("sNotes"));
			sOut.setCreateTime((Date)item.get("sCreateTime"));
		
			SmsRecordDetailOut out = new SmsRecordDetailOut();
			out.setTenantId((Integer)item.get("tenantId"));
			out.setAppCode((String)item.get("appCode"));
			out.setAppSerialNum((String)item.get("appSerialNum"));
			out.setMobiles((String)item.get("mobiles"));
			
			out.setReceiveRecord(rOut);
			out.setSendRecord(sOut);
			
			outs.add(out);
		}
		
		return new PageOut<SmsRecordDetailOut>(outs, totalCount);
	}
	
}
