package com.thc.platform.modules.sms.dto;

import lombok.Data;

@Data
public class SmsSendNodeTenantStatusListOut {

	private Integer tenantId;
	// 租户名称
	private String tenantName;
	// 发送节点ID
	private String smsSendNodeId;
	// 状态
	private Integer status;
}
