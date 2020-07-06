package com.thc.platform.modules.apppush.dto.android;

import java.util.Map;

import lombok.Data;

@Data
public class NotifyTemplateIn {

	// Android】格式yyyy-MM-dd HH:mm:ss, 收到消息后，在此时间区间展示，如果此区间APP不在前台，就会错过展示
	// 例如2019年8月14日8点-2019年8月14日9点，展示早间新闻
//	private DurationIn duration;
	// 消息推送的时候设置notifyid。如果需要覆盖此条消息，则下次使用相同的notifyid发一条新的消息。客户端sdk会根据notifyid进行覆盖。
//	private Integer notifyId;
	// 用于设置标题、内容、提示音、震动、背景图等
	private NotifyStyleIn style;
	
	/** 打开应用首页 */
	// 搭配transmissionContent使用，可选值为1、2；1：立即启动AP 2：客户端收到消息后需要自行处理
//	private Integer transmissionType;
	// 透传内容，不在通知栏中展示，开发者自行处理，不支持转义字符
	private Map<String, Object> transmissionContent;
		
	/** 打开应用内页面 */
	// 长度小于1000字节，通知带intent传递参数（以intent:开头，;end结尾）
	private String intent;
	
	/** 用于打开浏览器网页 */
	// 点击通知后打开的网页地址
//	private String url;
	
	public void validate() {
		if(transmissionContent == null || transmissionContent.isEmpty())
			transmissionContent = null;
		
//		if(StringUtil.isEmpty(intent)) 
//			intent = null;
//		
//		if(StringUtil.isEmpty(url)) 
//			url = null;
		
	}
	
}
