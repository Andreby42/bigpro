package com.thc.platform.modules.apppush.dto;

import java.util.List;

import com.thc.platform.modules.apppush.dto.android.NotifyTemplateIn;
import com.thc.platform.modules.apppush.dto.android.TransmissionTemplateIn;
import com.thc.platform.modules.apppush.dto.ios.IosTemplateIn;

import lombok.Data;

@Data
public class BatchSinglePushIn {
	
	private String appId;
	private List<CustomPush> items;
	
	// 是否保持离线消息
	private Boolean isOffline;
	// 过多久该消息离线失效（单位毫秒） 支持1-72小时*3600000毫秒
	private Long offlineExpireTime;
	// 推送网络要求 0:联网方式不限 1:仅wifi; 2:仅移动网络
	private Integer pushNetWorkType;
	  
	@Data
	public static class CustomPush {
		// 客户端 ID
		protected TargetIn target;
		
		/** Android 内容模版 */
		// 通知
		private NotifyTemplateIn notify;
		// 透传
		private TransmissionTemplateIn transmission;
		
		/** IOS 内容模版 */
		private IosTemplateIn ios;
	}
	
}
