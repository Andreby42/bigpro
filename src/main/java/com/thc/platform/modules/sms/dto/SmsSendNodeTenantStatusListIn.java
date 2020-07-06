package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.dto.PageIn;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SmsSendNodeTenantStatusListIn extends PageIn {

	private String smsSendNodeId;
	// 租户名称
	private String tenantName;
	
	public void validate() {
		super.validate();
		
		if(StringUtil.isEmpty(smsSendNodeId))
			throw BEUtil.failNormal("smsSendNodeId is empty");
		
	}
	
}
