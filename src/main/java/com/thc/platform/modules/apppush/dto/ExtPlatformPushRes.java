package com.thc.platform.modules.apppush.dto;

import lombok.Data;

@Data
public class ExtPlatformPushRes {

	// 成功标识
	private Boolean success = false;
	// 响应数据
	private String data;
	// 备注
	private String notes;
	
	
}
