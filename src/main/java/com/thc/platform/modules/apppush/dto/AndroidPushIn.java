package com.thc.platform.modules.apppush.dto;

import com.thc.platform.modules.apppush.dto.android.NotifyTemplateIn;

import lombok.Data;

@Data
public class AndroidPushIn {

	private String appId;
	// 客户端 ID
	private TargetIn target;
	// Android 发送模版
	private NotifyTemplateIn template;

	public void validate() {
		target.validate();
		template.validate();
	}
}