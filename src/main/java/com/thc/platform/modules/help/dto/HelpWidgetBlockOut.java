package com.thc.platform.modules.help.dto;

import java.util.List;

import lombok.Data;

@Data
public class HelpWidgetBlockOut {

	private String id;
	private Integer type;
	
	// 多条帮助
	private List<HelpMenuOut> helpMenus;
	
	// 单条帮助
	private HelpMenuOut helpMenu;
	private String helpNotes;
	
	// 视频帮组
	private HelpVideoOut video;
	
	@Data
	public static class HelpMenuOut {
		
		private String helpMenuId;
		private String helpMenuName;
		
	}
	
}
