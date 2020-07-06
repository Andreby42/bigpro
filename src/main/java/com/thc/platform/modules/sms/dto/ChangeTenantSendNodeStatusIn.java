package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class ChangeTenantSendNodeStatusIn {

	private Integer tenantId;
	private String smsSendNodeId;
	private Integer status;
	

	public void validate() {
		if(tenantId == null)
			throw BEUtil.illegalFormat("tenantId is null");
		
		if(StringUtil.isEmpty(smsSendNodeId))
			throw BEUtil.illegalFormat("smsSendNodeId is empty");
		
		if(status == null)
			throw BEUtil.illegalFormat("status is null");
		
		if(status != 1)
			status = 0;
	}
}
