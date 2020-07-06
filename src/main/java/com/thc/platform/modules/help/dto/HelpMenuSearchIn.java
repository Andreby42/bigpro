package com.thc.platform.modules.help.dto;

import com.thc.platform.common.dto.PageIn;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.entity.HelpVerEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
public class HelpMenuSearchIn extends PageIn {

	private String helpId;
	private String helpVer;
	
	private String name;
	
	public void validate() {
		super.validate();
		
		if(StringUtil.isEmpty(helpId))
			throw BEUtil.failNormal("helpId is empty");
		
		if(StringUtil.isEmpty(helpVer))
			helpVer = HelpVerEntity.DEFAULT_VER;
		
		if(StringUtil.isNotEmpty(name))
			name = name.trim();
	}
}
