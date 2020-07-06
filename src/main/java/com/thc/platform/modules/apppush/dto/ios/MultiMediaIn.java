package com.thc.platform.modules.apppush.dto.ios;

import lombok.Data;

@Data
public class MultiMediaIn {

	/** 资源类别：图片 */
	public static final int RES_TYPE_PIC = 1;
	/** 资源类别：音频 */
	public static final int RES_TYPE_AUDIO = 2;
	/** 资源类别：视频 */
	public static final int RES_TYPE_VIDEO = 3;
	
//	/** 是否在WIFI下才展示多媒体消息：否 */
//	public static final int IS_ONLY_WIFI_NO = 0;
//	/** 是否在WIFI下才展示多媒体消息：是 */
//	public static final int IS_ONLY_WIFI_YES = 1;
	
	// 资源id
	private String resId;
	// 资源url
	private String resUrl;
	// 资源类别
	private Integer resType;
	// 设置是否在WIFI下才展示多媒体消息，如果设置true但未使用WIFI时会展示成普通通知
	private Boolean isOnlyWifi;

}
