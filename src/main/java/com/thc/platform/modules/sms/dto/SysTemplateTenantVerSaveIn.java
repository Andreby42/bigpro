package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class SysTemplateTenantVerSaveIn {

	// 系统模版ID
	private String sysTemplateId;
	// 租户ID
	private Integer tenantId;
	// 内容 带参数
	private String content;
	
	public void validate() {
		if (StringUtil.isEmpty(sysTemplateId))
			throw BEUtil.illegalFormat("sysTemplateId is empty");
		
		if (tenantId == null)
			throw BEUtil.illegalFormat("tenantId is null");
		
		if (StringUtil.isEmpty(content))
			throw BEUtil.illegalFormat("content is empty");
		
	}
	
}
