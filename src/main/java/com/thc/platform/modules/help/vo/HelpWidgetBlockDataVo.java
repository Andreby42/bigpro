package com.thc.platform.modules.help.vo;

import java.util.List;

import lombok.Data;

@Data
public class HelpWidgetBlockDataVo {

	// 多条帮助
	private List<String> helpMenuIds;

	// 单条帮助
	private String helpMenuId;
	private String helpNotes;

	// 视频帮组
	private String videoId;

}
