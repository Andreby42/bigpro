package com.thc.platform.modules.help.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class HelpFeedbackIn {

	// 租户ID
	private Integer tenantId;
	// 菜单ID
	private String menuId;
	// 帮助内容
	private String content;
	// 创建人手机号
	private String creatorMobile;
	private String creatorId;
	private String creatorName;
	
	public void validate() {
		if(tenantId == null)
			throw BEUtil.illegalFormat("tenantId is null");
		
		if(StringUtil.isEmpty(menuId))
			throw BEUtil.illegalFormat("menuId is empty");
		
		if(StringUtil.isEmpty(content))
			throw BEUtil.illegalFormat("content is empty");
		
		if(StringUtil.isEmpty(creatorId))
			throw BEUtil.illegalFormat("creatorId is empty");
		
		if(StringUtil.isEmpty(creatorName))
			throw BEUtil.illegalFormat("creatorName is empty");
		
	}

}
