package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.dto.PageIn;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SmsRecordDetailQryIn extends PageIn {

	// 租户ID
	private Integer tenantId;
	// 发送应用编码
	private String appCode;
	// 发送业务流水号
	private String appSerialNum;
	// 手机号 
	private String mobile;

	public void validate() {
		super.validate();
	}
	
}
