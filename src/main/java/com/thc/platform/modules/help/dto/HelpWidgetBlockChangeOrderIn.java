package com.thc.platform.modules.help.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class HelpWidgetBlockChangeOrderIn {

	private String id;
	private String afterId;
	
	public void validate() {
		if(StringUtil.isEmpty(id))
			throw BEUtil.failNormal("id is empty");
		
	}
}
