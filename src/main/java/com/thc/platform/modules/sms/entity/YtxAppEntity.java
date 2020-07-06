package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 云通讯应用信息
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("sms_ytx_app")
public class YtxAppEntity extends ModifyBaseEntity {

	/** 状态：无效 */
	public static final int WHITE_LIST_STATUS_OFF = 0;
	/** 状态：有效 */
	public static final int WHITE_LIST_STATUS_ON = 1;
	
	@TableId(type = IdType.INPUT)
	private String id;
	// 应用ID
	private String appId;
	// 应用名称
	private String name;
	// 白名单状态
	private Integer whiteListStatus;
	// 账户ID
	private String accountId;
	// 备注信息
	private String notes;
	
}
