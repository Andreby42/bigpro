package com.thc.platform.modules.help.dto;

import java.util.ArrayList;
import java.util.List;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.entity.HelpWidgetBlockEntity;

import lombok.Data;

@Data
public class HelpWidgetBlockSaveIn {

	private String helpId;
	private String helpVer;
	
	private String menuId;
	
	private String helpWidgetBlockId;
	private Integer type;
	
	// 多条帮助
	private List<String> helpMenuIds;
	
	// 单条帮助
	private String helpMenuId;
	private String helpNotes;
	
	// 视频帮组
	private String videoId;
	
	public void validate() {
		if(StringUtil.isEmpty(helpWidgetBlockId)) {
			if(StringUtil.isEmpty(helpId) 
					|| StringUtil.isEmpty(helpVer)
					|| StringUtil.isEmpty(menuId))
				throw BEUtil.failNormal("新增时helpId，helpVer，menuId 不为空");
		}
		
		if(type == null)
			throw BEUtil.failNormal("type is null");
		
		if(type == HelpWidgetBlockEntity.TYPE_ONE_RICH_TEXT) {
			if(StringUtil.isEmpty(helpMenuId))
				throw BEUtil.failNormal("helpMenuId is empty");
			
			if(StringUtil.isEmpty(helpNotes))
				throw BEUtil.failNormal("helpNotes is empty");
			
		} else if(type == HelpWidgetBlockEntity.TYPE_MANY_RICH_TEXT) {
			if(helpMenuIds == null || helpMenuIds.size() == 0)
				throw BEUtil.failNormal("helpMenuIds is empty");
			
			List<String> tempMenuIds = new ArrayList<>();
			for(String tempMenuId : helpMenuIds) {
				if(StringUtil.isNotEmpty(tempMenuId))
					tempMenuIds.add(tempMenuId);
			}
			
			this.helpMenuIds = tempMenuIds;
			if(helpMenuIds.size() == 0)
				throw BEUtil.failNormal("helpMenuIds is empty");
			
		} else if(type == HelpWidgetBlockEntity.TYPE_VIDEO) {
			if(StringUtil.isEmpty(videoId))
				throw BEUtil.failNormal("videoId is empty");
		}
	}
	
}
