package com.thc.platform.modules.apppush.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("app_push_getui_config")
public class AppGeTuiConfigEntity {

	// App ID
	@TableId(type = IdType.INPUT)
	private String appId;
	// 个推 appId
	private String gtAppId;
	// 个推 appKey
	private String gtAppKey;
	// 个推 masterSecret
	private String gtMasterSecret;
	
}
