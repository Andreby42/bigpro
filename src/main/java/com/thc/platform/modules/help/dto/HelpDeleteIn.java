package com.thc.platform.modules.help.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class HelpDeleteIn {

	private String helpId;
	private String helpVer;
	
	public void validate() {
		if(StringUtil.isEmpty(helpId))
			throw BEUtil.failNormal("helpId is empty");
		
		if(StringUtil.isEmpty(helpVer))
			throw BEUtil.failNormal("helpVer is empty");
		
	}
	
}
