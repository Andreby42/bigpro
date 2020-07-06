package com.thc.platform.modules.help.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HelpFeedbackOut {

	// ID
	private String id;
	// 租户ID
	private Integer tenantId;
	// 租户名称
	private String tenantName;
	// 帮助ID
	private String helpId;
	// 帮助ID
	private String helpName;
	// 帮助版本
	private String helpVer;
	// 菜单ID
	private String menuId;
	// 菜单ID
	private String menuName;
	// 帮助内容
	private String content;
	// 创建人手机号
	private String creatorMobile;
	private String creatorId;
	private String creatorName;
	private Date createTime;
}
