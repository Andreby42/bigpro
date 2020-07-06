package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 云通讯模版信息
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("sms_ytx_template")
public class YtxTemplateEntity extends ModifyBaseEntity {

	/** 模版类型：验证码 */
	public static final int TYPE_VERIFY = 0;
	/** 模版类型：通知 */
	public static final int TYPE_NOTICE = 1;
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 荣联模版ID
	private String templateId;
	// 模版类型
	private Integer type;
	// 应用ID
	private String appId;
	// 模版签名ID
	private String signTypeId;
	// 备注信息
	private String notes;
	
}
