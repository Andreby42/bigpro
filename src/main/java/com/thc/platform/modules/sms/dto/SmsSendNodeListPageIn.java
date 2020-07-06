package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.dto.PageIn;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SmsSendNodeListPageIn extends PageIn {

	private String code;
	private String name;
	
	public void validate() {
		super.validate();
	}
	
}
