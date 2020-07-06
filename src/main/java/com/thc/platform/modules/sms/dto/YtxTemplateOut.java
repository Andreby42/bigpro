package com.thc.platform.modules.sms.dto;

import com.thc.platform.modules.sms.entity.TemplateSignTypeEntity;
import com.thc.platform.modules.sms.entity.YtxTemplateEntity;

public class YtxTemplateOut {

	private YtxTemplateEntity entity;
	private TemplateSignTypeEntity signType;
	
	public YtxTemplateOut(YtxTemplateEntity entity, TemplateSignTypeEntity signType) {
		this.entity = entity;
		this.signType = signType;
	}
	
	public String getId() {
		return entity.getId();
	}
	
	public String getTemplateId() {
		return entity.getTemplateId();
	}
	
	public Integer getType() {
		return entity.getType();
	}
	
	public String getAppId() {
		return entity.getAppId();
	}
	
	public String getSignTypeId() {
		return entity.getSignTypeId();
	}
	
	public String getSignTypeName() {
		return signType.getName();
	}
	
	public String getNotes() {
		return entity.getNotes();
	}
	
}
