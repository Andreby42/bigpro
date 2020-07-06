package com.thc.platform.modules.apppush.util;

import com.gexin.rp.sdk.template.AbstractTemplate;
//import com.gexin.rp.sdk.template.LinkTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
//import com.gexin.rp.sdk.template.StartActivityTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.AbstractNotifyStyle;
import com.gexin.rp.sdk.template.style.Style0;
//import com.gexin.rp.sdk.template.style.Style6;
//import com.thc.platform.modules.apppush.dto.android.DurationIn;
import com.thc.platform.modules.apppush.dto.android.NotifyStyleIn;
import com.thc.platform.modules.apppush.dto.android.NotifyTemplateIn;
import com.thc.platform.modules.apppush.dto.android.TransmissionTemplateIn;

public class GTAndroidUtil {

	public static TransmissionTemplate getTransmissionTemplate(TransmissionTemplateIn in, String appId, String appKey) {
		TransmissionTemplate t = new TransmissionTemplate();
		t.setAppId(appId);
		t.setAppkey(appKey);
		
		t.setTransmissionContent(in.getContent());
		t.setTransmissionType(in.getType());
		
		return t;
	}
	
	public static AbstractTemplate getNotifyTemplate(NotifyTemplateIn in, String appId, String appKey) throws Exception {
//		if(StringUtil.isNotEmpty(in.getUrl()))
//			return getLinkTemplate(in, appId, appKey);
//		else if(StringUtil.isNotEmpty(in.getIntent()))
//			return getStartActivityTemplate(in, appId, appKey);
//		else
			return getNotificationTemplate(in, appId, appKey);
	}
	
//	private static LinkTemplate getLinkTemplate(NotifyTemplateIn in, String appId, String appKey) throws Exception {
//		LinkTemplate t = new LinkTemplate();
//		t.setAppId(appId);
//		t.setAppkey(appKey);
//		
//		DurationIn dIn = in.getDuration();
//		if(dIn != null) 
//			t.setDuration(dIn.getBegin(), dIn.getEnd());
//		
//		if(in.getStyle() != null) {
//			AbstractNotifyStyle style = getNotifyStyle(in.getStyle());
//			t.setStyle(style);
//		}
//		
//		if(in.getNotifyId() != null)
//			t.setNotifyid(in.getNotifyId());
//		
//		t.setUrl(in.getUrl());
//		
//		return t;
//	}
	
//	private static StartActivityTemplate getStartActivityTemplate(NotifyTemplateIn in, String appId, String appKey) throws Exception {
//		StartActivityTemplate t = new StartActivityTemplate();
//		t.setAppId(appId);
//		t.setAppkey(appKey);
//		
//		DurationIn dIn = in.getDuration();
//		if(dIn != null) 
//			t.setDuration(dIn.getBegin(), dIn.getEnd());
//		
//		if(in.getStyle() != null) {
//			AbstractNotifyStyle style = getNotifyStyle(in.getStyle());
//			t.setStyle(style);
//		}
//		
//		if(in.getNotifyId() != null)
//			t.setNotifyid(in.getNotifyId());
//		
//		t.setIntent(in.getIntent());
//		
//		return t;
//	}
	
	private static NotificationTemplate getNotificationTemplate(NotifyTemplateIn in, String appId, String appKey) throws Exception {
		NotificationTemplate t = new NotificationTemplate();
		t.setAppId(appId);
		t.setAppkey(appKey);
		
//		DurationIn dIn = in.getDuration();
//		if(dIn != null) 
//			t.setDuration(dIn.getBegin(), dIn.getEnd());
		
		if(in.getStyle() != null) {
			AbstractNotifyStyle style = getNotifyStyle(in.getStyle());
			t.setStyle(style);
		}
		
//		if(in.getNotifyId() != null)
//			t.setNotifyid(in.getNotifyId());
//		
//		if(StringUtil.isNotEmpty(in.getTransmissionContent())) {
//			//搭配transmissionContent使用，可选值为1、2；1：立即启动APP 2：客户端收到消息后需要自行处理
//			t.setTransmissionType(2);
//			t.setTransmissionContent(in.getTransmissionContent());
//		}
		
		return t;
	}
	
	private static AbstractNotifyStyle getNotifyStyle(NotifyStyleIn in) {
//		if(in.getBigImageUrl() != null || in.getBigText() != null)
//			return getStyle6(in);
//		else
			return getStyle0(in); 
	}
	
//	private static Style6 getStyle6(NotifyStyleIn in) {
//		Style6 s = new Style6();
//		// 公共参数
//		if(in.getRing() != null)
//			s.setRing(in.getRing());
//		
//		if(in.getVibrate() != null)
//			s.setVibrate(in.getVibrate());
//		
//		if(in.getClearable() != null)
//			s.setClearable(in.getClearable());
//		
//		// 系统参数
//		s.setTitle(in.getTitle());
//		s.setText(in.getText());
//		
//		if(in.getLogo() != null)
//			s.setLogo(in.getLogo());
//		
//		if(in.getLogoUrl() != null)
//			s.setLogoUrl(in.getLogoUrl());
//		
//		if(in.getChannel() != null)
//			s.setChannel(in.getChannel());
//		
//		if(in.getChannelName() != null)
//			s.setChannelName(in.getChannelName());
//		
//		if(in.getChannelLevel() != null)
//			s.setChannelLevel(in.getChannelLevel());
//		
//		// Style6-1：大图+文本样式
//		if(in.getBigImageUrl() != null)
//			s.setBigStyle1(in.getBigImageUrl());
//		// Style6-2：长文本样式
//		if(in.getBigText() != null)
//			s.setBigStyle2(in.getBigText());
//		
//		return s;
//	}
	
	private static Style0 getStyle0(NotifyStyleIn in) {
		Style0 s = new Style0();
		// 公共参数
//		if(in.getRing() != null)
//			s.setRing(in.getRing());
//		
//		if(in.getVibrate() != null)
//			s.setVibrate(in.getVibrate());
//		
//		if(in.getClearable() != null)
//			s.setClearable(in.getClearable());
		
		// 系统参数
		s.setTitle(in.getTitle());
		s.setText(in.getText());
		
//		if(in.getLogo() != null)
//			s.setLogo(in.getLogo());
//		
//		if(in.getLogoUrl() != null)
//			s.setLogoUrl(in.getLogoUrl());
//		
//		if(in.getChannel() != null)
//			s.setChannel(in.getChannel());
//		
//		if(in.getChannelName() != null)
//			s.setChannelName(in.getChannelName());
//		
//		if(in.getChannelLevel() != null)
//			s.setChannelLevel(in.getChannelLevel());
		
		return s;
	}
}
