package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 云通讯账户信息
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("sms_ytx_account")
public class YtxAccountEntity extends ModifyBaseEntity {

	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 名称
	private String name;
	// 账户ID
	private String accountSid;
	// 应用ID
	private String authToken;
	// rest服务地址
	private String url;
	// rest服务端口号
	private String port;
	// 账号备注信息
	private String notes;
	
}
