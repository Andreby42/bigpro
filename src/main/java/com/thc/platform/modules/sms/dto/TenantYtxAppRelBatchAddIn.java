package com.thc.platform.modules.sms.dto;

import java.util.List;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class TenantYtxAppRelBatchAddIn {

	// 云通讯 App ID
	private String appId;
	// 租户ID
	private List<Integer> tenantIds;
	
	public void validate() {
		if(StringUtil.isEmpty(appId))
			throw BEUtil.failNormal("appId is empty.");
		
		if(tenantIds == null || tenantIds.isEmpty())
			throw BEUtil.failNormal("tenantIds is empty.");
	}
	
}
