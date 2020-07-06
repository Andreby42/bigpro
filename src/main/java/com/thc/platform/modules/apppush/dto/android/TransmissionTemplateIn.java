package com.thc.platform.modules.apppush.dto.android;

import lombok.Data;

@Data
public class TransmissionTemplateIn {

//	/** 类型：立即启动APP（不推荐使用，影响客户体验） */
//	public static final int TYPE_START_APP_NOW = 1;
//	/** 类型：客户端收到消息后需要自行处理 */
//	public static final int TYPE_CUSTOM_PROCESS = 2;
	
	// 透传类型
	private Integer type;
	// 透传内容，不支持转义字符
	private String content;
	
}
