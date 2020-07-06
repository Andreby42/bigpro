package com.thc.platform.modules.help.dto;

import com.thc.platform.common.dto.PageIn;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
public class HelpVerListIn extends PageIn {

	private String helpId;
	
	public void validate() {
		super.validate();
		
		if(StringUtil.isEmpty(helpId))
			throw BEUtil.failNormal("helpId is empty.");
		
	}
	
}
