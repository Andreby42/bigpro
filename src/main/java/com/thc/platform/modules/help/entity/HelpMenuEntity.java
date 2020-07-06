package com.thc.platform.modules.help.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 帮助菜单
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("help_menu")
public class HelpMenuEntity extends ModifyBaseEntity {

	public static final int DELETE_STATUS_NO = 0;
	public static final int DELETE_STATUS_YES = 1;
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 上级ID
	private String rootId;
	// 名称
	private String name;
	// 帮助ID
	private String helpVerId;
	// 页面ID
	private String pageId;
	// 前面 WidgetBlock ID
	private String prevId;
	// 后面 WidgetBlock ID
	private String nextId;
	// 删除状态
	private Integer deleteStatus = DELETE_STATUS_NO;
	
}
