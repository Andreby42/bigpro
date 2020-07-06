package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 租户与云通讯模版关系实体
 */
@Data
@TableName("sms_tenant_ytx_app_rel")
public class TenantYtxAppRelEntity {

	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 租户ID
	private Integer tenantId;
	// 云通讯 App ID
	private String appId;
	
}
