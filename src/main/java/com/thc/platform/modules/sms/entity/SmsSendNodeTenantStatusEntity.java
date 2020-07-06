package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 短信发送节点-租户状态实体
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("sms_send_node_tenant_status")
public class SmsSendNodeTenantStatusEntity extends ModifyBaseEntity {

	public static final int STATUS_OFF = 0;
	public static final int STATUS_ON = 1;
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 租户ID
	private Integer tenantId;
	// 短信发送节点ID
	private String smsSendNodeId;
	// 开通状态
	private Integer status;
	
	
}
