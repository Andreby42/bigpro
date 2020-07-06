package com.thc.platform.modules.apppush.dto;

import com.thc.platform.modules.apppush.dto.ios.IosTemplateIn;

import lombok.Data;

@Data
public class IosTransmissionWithApnIn {

	private String appId;
	// 客户端 ID
	private TargetIn target;
	// IOS 发送模版
	private IosTemplateIn template;
	
	public void validate() {
		target.validate();
		
		template.validate();
	}
}
