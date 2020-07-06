package com.thc.platform.modules.help.dto;

import java.util.List;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.entity.HelpVerEntity;

import lombok.Data;

@Data
public class HelpSaveIn {

	private String helpId;
	private String helpVer;
	
	private String rootMenuId;
	private String menuId;
	private String menuName;
	
	private List<String> helpVideoIds;
	private String richText;
	
	public void validate() {
		if(StringUtil.isEmpty(menuName)) 
			throw BEUtil.failNormal("menuName 不允许为空");
		
		if(StringUtil.isEmpty(helpVer))
			helpVer = HelpVerEntity.DEFAULT_VER;
		
		if(StringUtil.isEmpty(rootMenuId) && StringUtil.isEmpty(menuId)
				&& (StringUtil.isEmpty(helpId) || StringUtil.isEmpty(helpVer)))
			throw BEUtil.failNormal("新增一级菜单时，helpId 和 helpVer 的值不允许为空");
		
	}
	
}
