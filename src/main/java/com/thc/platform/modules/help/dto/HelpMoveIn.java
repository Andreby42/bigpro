package com.thc.platform.modules.help.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class HelpMoveIn {

	private String id;
	private String afterId;
	private String parentId;
	
	public void validate() {
		if(StringUtil.isEmpty(id))
			throw BEUtil.failNormal("id is empty");
	}
	
}
