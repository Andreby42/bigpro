package com.thc.platform.modules.help.dto;

import java.util.List;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.entity.HelpVerEntity;

import lombok.Data;

@Data
public class HelpVideoSaveIn {

	// ID
	private String id;
	// 名称
	private String name;
	// 视频URL
	private String videoUrl;
	// 视频缩略图URL
	private String videoThumbnailUrl;
	// 视频字幕文件URL
	private String videoCaptionFileUrl;
	// 帮助ID
	private String helpId;
	// 帮助版本
	private String helpVer;
	// 标签
	private List<String> tags;
	// 简介
	private String briefIntroduction;
	
	public void validate() {
		if(StringUtil.isEmpty(name))
			throw BEUtil.failNormal("name is empty.");
		
		if(StringUtil.isEmpty(videoUrl))
			throw BEUtil.failNormal("videoUrl is empty.");
		
//		if(StringUtil.isEmpty(videoThumbnailUrl))
//			throw BEUtil.failNormal("videoThumbnailUrl is empty.");
		
//		if(StringUtil.isEmpty(helpId))
//			throw BEUtil.failNormal("helpId is empty.");
		
		if(StringUtil.isEmpty(helpVer))
			helpVer = HelpVerEntity.DEFAULT_VER;
	}
	
}
