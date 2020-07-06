package com.thc.platform.modules.apppush.dto;

import java.util.List;

import com.thc.platform.modules.apppush.dto.android.NotifyTemplateIn;
import com.thc.platform.modules.apppush.dto.android.TransmissionTemplateIn;
import com.thc.platform.modules.apppush.dto.ios.IosTemplateIn;

import lombok.Data;

@Data
public class AppBatchPushIn {
	
	private String appId;
	
	// 客户端 ID
	protected List<String> clientIds;
	
	/** Android 内容模版 */
	// 通知
	private NotifyTemplateIn notify;
	// 透传
	private TransmissionTemplateIn transmission;
	
	/** IOS 内容模版 */
	private IosTemplateIn ios;
	
	public boolean isIOS() {
		return ios != null;
	}
	
	public boolean isAndroid() {
		return notify != null || transmission != null;
	}
	
}
