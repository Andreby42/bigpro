package com.thc.platform.modules.help.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 帮助版本
 */
@Data
@TableName("help_ver")
public class HelpVerEntity {

	public static final String DEFAULT_VER = "default";

	public static final int DELETE_STATUS_NO = 0;
	public static final int DELETE_STATUS_YES = 1;
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 帮助ID
	private String helpId;
	// 版本
	private String ver;
	// 来源版本
	private String srcVer;
	// 删除状态
	private Integer deleteStatus = DELETE_STATUS_NO;
}
