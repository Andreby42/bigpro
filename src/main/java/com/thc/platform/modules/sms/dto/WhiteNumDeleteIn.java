package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class WhiteNumDeleteIn {

	private String ytxAppId;
	private String mobile;
	
	public void validate() {
		if (StringUtil.isEmpty(ytxAppId))
			throw BEUtil.illegalFormat("ytxAppId is empty");
		
		if (StringUtil.isEmpty(mobile))
			throw BEUtil.illegalFormat("mobile is empty");
		
	}
	
}
