package com.thc.platform.modules.help.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class HelpPageEditOut {

	private String menuId;
	private String menuName;
	// 富文本信息
	private String richText;
	private Date modifyTime;
	// 视频信息
	private List<VideoOut> videos;
	
	@Data
	public static class VideoOut {

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
		// 标签
//		private String tags;
//		 简介
		private String briefIntroduction;
	}
	
}
