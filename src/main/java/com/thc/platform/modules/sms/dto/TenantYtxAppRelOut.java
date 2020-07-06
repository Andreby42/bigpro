package com.thc.platform.modules.sms.dto;

import com.thc.platform.modules.sms.entity.TenantYtxAppRelEntity;

public class TenantYtxAppRelOut {

	private TenantYtxAppRelEntity entity;
	private String tenantName;
	
	public TenantYtxAppRelOut(TenantYtxAppRelEntity entity, String tenantName) {
		this.entity = entity;
		this.tenantName = tenantName;
	}
	
	public String getId() {
		return entity.getId();
	}
	
	public Integer getTenantId() {
		return entity.getTenantId();
	}

	public String getTenantName() {
		return tenantName;
	}
	
}
