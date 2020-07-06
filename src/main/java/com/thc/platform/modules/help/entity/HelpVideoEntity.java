package com.thc.platform.modules.help.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 帮助视频
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("help_video")
public class HelpVideoEntity extends ModifyBaseEntity {

	public static final int DELETE_STATUS_NO = 0;
	public static final int DELETE_STATUS_YES = 1;
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 名称
	private String name;
	// 视频URL
	private String videoUrl;
	// 视频缩略图URL
	private String videoThumbnailUrl;
	// 视频字幕文件URL
	private String videoCaptionFileUrl;
//	// 系统ID
//	private String systemId;
//	// 系统版本
//	private String systemVer;
	// 帮助ID
	private String helpVerId;
	// 标签
	private String tags;
	// 简介
	private String briefIntroduction;
	// 删除状态
	private Integer deleteStatus = DELETE_STATUS_NO;
}
