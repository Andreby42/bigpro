package com.thc.platform.modules.help.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.CreateBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 帮助
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("help_user_feedback")
public class HelpFeedbackEntity extends CreateBaseEntity {

	// ID
	@TableId(type = IdType.INPUT)
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
}
