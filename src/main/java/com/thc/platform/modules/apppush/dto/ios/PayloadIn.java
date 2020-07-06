package com.thc.platform.modules.apppush.dto.ios;

import java.util.Map;

import lombok.Data;

@Data
public class PayloadIn {

	// 设置角标，还可以实现显示数字的自动增减，如"+1"、"-1"、"1"等
//	private String autoBadge;
	// 推送直接带有透传数据
//	private Integer contentAvailable;
	// 通知消息体
//	private String simpleAlert;
	// Dictionary Alert
	private DictionaryAlertIn dictionaryAlert;
	// 通知铃声文件名
//	private String simpleSound;
	// sound支持Dictionary类型，可以控制“警告性质的推送”，仅支持iOS 12.0以上版本
//	private DictionarySoundIn dictionarySound;
	// 在客户端通知栏触发特定的action和button显示
//	private String category;
	// 自定义的数据,Key-Value形式
	private Map<String, Object> customMsg;
	// 多媒体资源，当前最多传入3个资源
//	private List<MultiMediaIn> multiMedias;
	// 设置语音播报类型,int类型，0.不可用 1.播放body 2.播放自定义文本
//	private Integer voicePlayType;
	// 设置语音播报内容，String类型，非必须参数，用户自定义播放内容，仅在voicePlayMessage=2时生效
//	private String voicePlayMessage;
	// 通知分组的特定于应用程序的标识符，仅支持iOS 12.0以上版本
//	private String threadId;
	// 使用相同的apnsCollapseId可以覆盖之前的消息
//	private String apnsCollapseId;
	
	public void validate() {
		if(dictionaryAlert != null)
			dictionaryAlert.validate();
		
	}
	
	public boolean isEmpty() {
		if(dictionaryAlert == null) {
			return customMsg == null || customMsg.isEmpty();
		}
		return false;
	}
	
}
