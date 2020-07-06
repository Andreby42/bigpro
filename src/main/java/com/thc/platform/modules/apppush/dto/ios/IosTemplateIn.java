package com.thc.platform.modules.apppush.dto.ios;

import java.util.Map;

import com.thc.platform.common.util.BEUtil;

import lombok.Data;

@Data
public class IosTemplateIn {

	// 透传内容，不支持转义字符
	private Map<String, Object> transmissionContent;
	// IOS 用于设置标题、内容、语音、多媒体、VoIP（基于IP的语音传输）等。离线走APNs时起效果，具体样式见iOS通知样式
	private PayloadIn payload;

	public void validate() {
		payload.validate();
		
		if(transmissionContent == null || transmissionContent.isEmpty()) {
			if(payload.isEmpty())
				throw BEUtil.failNormal("push an empty msg.");
		}
	}
	
}
