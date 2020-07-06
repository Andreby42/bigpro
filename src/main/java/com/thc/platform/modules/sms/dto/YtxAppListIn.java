package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.dto.PageIn;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class YtxAppListIn extends PageIn {

	// 账户ID
	private String accountId;
	// 关键字
	private String keyword;
	
	public void validate() {
		super.validate();
	}
}
