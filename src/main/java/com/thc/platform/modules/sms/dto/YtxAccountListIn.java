package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.dto.PageIn;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class YtxAccountListIn extends PageIn {

	private String keyword;
	
	public void validate() {
		super.validate();
	}
}
