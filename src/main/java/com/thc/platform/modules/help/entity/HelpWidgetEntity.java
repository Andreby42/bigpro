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
@TableName("help_widget")
public class HelpWidgetEntity extends ModifyBaseEntity {

	@TableId(type = IdType.INPUT)
	private String id;
	// 版本版本ID
	private String helpVerId;
	// 关联菜单ID
	private String menuId;

}
