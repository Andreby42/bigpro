package com.thc.platform.modules.sms.dto;

import java.util.Date;

import com.thc.platform.modules.sms.entity.SysTemplateEntity;

public class SysTemplateListOut {

	private SysTemplateEntity template;
	
	public SysTemplateListOut(SysTemplateEntity template) {
		this.template = template;
	}

	public String getId() {
		return template.getId();
	}
	
	public String getCode() {
		return template.getCode();
	}
	
	public Integer getSmsType() {
		return template.getSmsType();
	}
	
	public String getName() {
		return template.getName();
	}
	
	public String getContent() {
		return template.getContent();
	}
	
	public String getModifyUserId() {
		return template.getModifyUserId();
	}
	
	public String getModifyUserName() {
		return template.getModifyUserName();
	}
	
	public Date getModifyTime() {
		return template.getModifyTime();
	}
	
	public String getCreatorId() {
		return template.getCreatorId();
	}
	
	public String getCreatorName() {
		return template.getCreatorName();
	}
	
	public Date getCreateTime() {
		return template.getCreateTime();
	}
}
