package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 短信系统模板实体
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("sms_sys_template")
public class SysTemplateEntity extends ModifyBaseEntity {
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 编码
	private String code;
	// 模版类型
	private Integer smsType;
	// 名称
	private String name;
	// 内容 带参数
	private String content;
	
}
