package com.thc.platform.modules.apppush.dto.android;

import lombok.Data;

@Data
public class NotifyStyleIn {

	// 收到通知是否响铃：true响铃，false不响铃。默认响铃。
//	private Boolean ring;
	// 收到通知是否振动：true振动，false不振动。默认振动。
//	private Boolean vibrate;
	// 通知是否可清除： true可清除，false不可清除。默认可清除。
//	private Boolean clearable;
	
	// 通知标题
	private String title;
	// 通知内容
	private String text;
	// 通知的图标名称，包含后缀名（需要在客户端开发时嵌入），如“icon.png”
	// 小LOGO，默认push_small.png，需要提前内置到客户端
//	private String logo = "";
	// 通知图标URL地址，小米、华为有些机型不支持此参数
//	private String logoUrl = "";
	
	// 通知展示大图样式，参数是大图的URL地址
//	private String bigImageUrl = "";
	
	// 通知展示文本+长文本样式，参数是长文本
//	private String bigText = "";

	/** 渠道设置 */
	// 通知渠道id，唯一标识 默认值：Default
//	private String channel;
	// 通知渠道名称 默认值：Default
//	private String channelName;
	// 该字段代表通知渠道重要性，具体值有0、1、2、3、4；默认值3
	// 设置之后不能修改；具体展示形式如下：
	// 0：无声音，无震动，不显示。(不推荐)
	// 1：无声音，无震动，锁屏不显示，通知栏中被折叠显示，导航栏无logo。
	// 2：无声音，无震动，锁屏和通知栏中都显示，通知不唤醒屏幕。
	// 3：有声音，有震动，锁屏和通知栏中都显示，通知唤醒屏幕。（推荐）
	// 4：有声音，有震动，亮屏下通知悬浮展示，锁屏通知以默认形式展示且唤醒屏幕。（推荐）
//	private Integer channelLevel = 3;
}