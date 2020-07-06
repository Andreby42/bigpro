package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class SmsSendNodeSaveIn {

	private String id;
	private String code;
	private String name;
	
	public void validate() {
		if(StringUtil.isEmpty(code))
			throw BEUtil.illegalFormat("code is empty");
		
		if(StringUtil.isEmpty(name))
			throw BEUtil.illegalFormat("name is empty");
	}
	
}
