package com.thc.platform.modules.help.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class HelpCopyIn {

	private String helpId;
	private String srcHelpVer;
	private String targetHelpVer;
	
	public void validate() {
		if(StringUtil.isEmpty(helpId))
			throw BEUtil.failNormal("helpId is empty");
		
		if(StringUtil.isEmpty(srcHelpVer))
			throw BEUtil.failNormal("srcHelpVer is empty");
		
		if(StringUtil.isEmpty(targetHelpVer))
			throw BEUtil.failNormal("targetHelpVer is empty");
	}
	
}
