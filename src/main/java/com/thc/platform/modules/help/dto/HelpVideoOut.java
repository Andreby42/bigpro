package com.thc.platform.modules.help.dto;

import java.util.Date;
import java.util.List;

import lombok.Data;

@Data
public class HelpVideoOut {

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
	// 更新时间
	private Date modifyTime;
	// 上传时间
	private Date createTime;

}
