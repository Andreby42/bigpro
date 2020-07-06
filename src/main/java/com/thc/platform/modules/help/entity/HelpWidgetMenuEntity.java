package com.thc.platform.modules.help.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 帮助Widget菜单
 */
@Data
@TableName("help_widget_menu")
public class HelpWidgetMenuEntity {

	public static final String ROOT_ID = "0";
	
	// ID
	private String menuId;
	// 上级ID
	@TableField("parent_id")
	private String rootId;
	// 名称
	private String name;
	// 帮助ID
	private String helpVerId;

}
