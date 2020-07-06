package com.thc.platform.modules.help.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("help_widget_block")
public class HelpWidgetBlockEntity extends ModifyBaseEntity {

	public static final int TYPE_ONE_RICH_TEXT = 0;
	public static final int TYPE_MANY_RICH_TEXT = 1;
	public static final int TYPE_VIDEO = 2;
	
	@TableId(type = IdType.INPUT)
	private String id;
	// 归属Widget ID
	private String widgetId;
	private String helpVerId;
	// 类型
	private Integer type;
	// 数据
	private String data;
	// 前面 WidgetBlock ID
	private String prevId;
	// 后面 WidgetBlock ID
	private String nextId;

}
