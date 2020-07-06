package com.thc.platform.modules.sms.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.CreateBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 云通讯应用白名单
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("sms_white_num")
public class WhiteNumEntity extends CreateBaseEntity {

	private String ytxAppId;
	private String mobile;
	private String notes;
	
}
