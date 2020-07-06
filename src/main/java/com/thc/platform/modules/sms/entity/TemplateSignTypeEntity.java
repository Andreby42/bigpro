package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 云通讯模版签名ID
 */
@Data
@TableName("sms_template_sign_type")
public class TemplateSignTypeEntity {

	public static final String ID_DEFAULT = "default";
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 名称
	private String name;
	// 备注
	private String notes;
	
}
