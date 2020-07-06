package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 短信发送节点实体
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("sms_send_node")
public class SmsSendNodeEntity extends ModifyBaseEntity {

	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 编码
	private String code;
	// 名称
	private String name;

}
