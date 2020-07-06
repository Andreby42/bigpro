package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 短信系统模板-租户定制版本实体
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("sms_sys_template_tenant_ver")
public class SysTemplateTenantVerEntity extends ModifyBaseEntity {

	// ID
	@TableId(type = IdType.INPUT)
	private String sysTemplateId;
	// 租户ID
	@TableId(type = IdType.INPUT)
	private Integer tenantId;
	// 内容 带参数
	private String content;
	
}
