package com.thc.platform.modules.help.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 帮助页面
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("help_page")
public class HelpPageEntity extends ModifyBaseEntity {

	public static final int DELETE_STATUS_NO = 0;
	public static final int DELETE_STATUS_YES = 1;
	
	@TableId(type = IdType.INPUT)
	private String id;
	// 富文本信息
	private String richText;
	// 创建帮助版本ID
	private String createHelpVerId;
	// 删除状态
	private Integer deleteStatus = DELETE_STATUS_NO;
}
