package com.thc.platform.modules.apppush.dto;

import java.util.Map;

import com.thc.platform.common.util.BEUtil;

import lombok.Data;

@Data
public class TransmissionIn {

	private String appId;
	// 客户端 ID
	private TargetIn target;
	// 透传内容
	private Map<String, Object> transmissionContent;
	
	public void validate() {
		if(target == null)
			throw BEUtil.failNormal("target is null");
		
		target.validate();
		
		if(transmissionContent == null || transmissionContent.isEmpty())
			throw BEUtil.failNormal("transmissionContent is empty");
		
	}
	
}
