package com.thc.platform.modules.apppush.util;

import java.util.Map;

import com.gexin.fastjson.JSON;

//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;

import com.gexin.rp.sdk.base.payload.APNPayload;
//import com.gexin.rp.sdk.base.payload.MultiMedia;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.thc.platform.modules.apppush.dto.ios.DictionaryAlertIn;
//import com.thc.platform.modules.apppush.dto.ios.DictionarySoundIn;
import com.thc.platform.modules.apppush.dto.ios.IosTemplateIn;
//import com.thc.platform.modules.apppush.dto.ios.MultiMediaIn;
import com.thc.platform.modules.apppush.dto.ios.PayloadIn;

public class GTIosUtil {

	public static TransmissionTemplate getTemplate(IosTemplateIn in, String appId, String appKey) {
		TransmissionTemplate transmission = new TransmissionTemplate();
		transmission.setAppId(appId);
		transmission.setAppkey(appKey);
		
		transmission.setTransmissionType(GTUtil.TYPE_CUSTOM_PROCESS);
		
		Map<String,Object> transmissionContent = in.getTransmissionContent();
		if(transmissionContent != null && !transmissionContent.isEmpty())
			transmission.setTransmissionContent(JSON.toJSONString(transmissionContent));
//		transmission.setTransmissionContent(in.getContent());
		
		PayloadIn pIn = in.getPayload();
		if(in.getPayload() != null) {
			APNPayload apnPayload = new APNPayload();
			
//			if(StringUtil.isNotEmpty(pIn.getAutoBadge()))
//				apnPayload.setAutoBadge(pIn.getAutoBadge());
//			
//			if(pIn.getContentAvailable() != null)
//				apnPayload.setContentAvailable(pIn.getContentAvailable());
			
			APNPayload.AlertMsg alertMsg = getAlertMsg(pIn);
			apnPayload.setAlertMsg(alertMsg);
			
//			if(StringUtil.isNotEmpty(pIn.getSimpleSound())) {
//				apnPayload.setSound(pIn.getSimpleSound());
//			} else {
//				DictionarySoundIn dsIn = pIn.getDictionarySound();
//				if(dsIn != null)
//					apnPayload.setSound(getSound(dsIn));
//			}
//			apnPayload.setCategory(pIn.getCategory());
			
//			List<MultiMediaIn> mmIns = pIn.getMultiMedias();
//			List<MultiMedia> multiMedias = getMultiMedias(mmIns);
//			apnPayload.setMultiMedias(multiMedias);
			
			Map<String, Object> customMsg = pIn.getCustomMsg();
			if(customMsg != null) {
				for(Map.Entry<String, Object> entry : customMsg.entrySet())
					apnPayload.addCustomMsg(entry.getKey(), entry.getValue());
			}
//			if(pIn.getVoicePlayType() != null)
//				apnPayload.setVoicePlayType(pIn.getVoicePlayType());
			
//			apnPayload.setVoicePlayMessage(pIn.getVoicePlayMessage());
//			apnPayload.setThreadId(pIn.getThreadId());
//			apnPayload.setApnsCollapseId(pIn.getApnsCollapseId());
			
			transmission.setAPNInfo(apnPayload);
		}
		
		return transmission;
	}
	
	private static APNPayload.AlertMsg getAlertMsg(PayloadIn pIn) {
//		if(StringUtil.isNotEmpty(pIn.getSimpleAlert())) 
//			return new APNPayload.SimpleAlertMsg(pIn.getSimpleAlert());
		
		DictionaryAlertIn daIn = pIn.getDictionaryAlert();
		
		return getDictionaryAlertMsg(daIn);
	}
	
	private static APNPayload.DictionaryAlertMsg getDictionaryAlertMsg(DictionaryAlertIn in) {
		APNPayload.DictionaryAlertMsg am = new APNPayload.DictionaryAlertMsg();
		
		am.setBody(in.getBody());
//		am.setActionLocKey(in.getActionLocKey());
//		am.setLocKey(in.getLocKey());
		
//		List<String> locArgs = in.getLocArgs();
//		if(locArgs != null && !locArgs.isEmpty()) 
//			for(String arg : locArgs)
//				am.addLocArg(arg);

//		am.setLaunchImage(in.getLaunchImage());
		am.setTitle(in.getTitle());
//		am.setTitleLocKey(in.getTitleLocKey());
		
//		List<String> titleLocArgs = in.getTitleLocArgs();
//		if(titleLocArgs != null && !titleLocArgs.isEmpty()) 
//			for(String arg : titleLocArgs)
//				am.addTitleLocArg(arg);
//		
//		am.setSubtitle(in.getSubTitle());
//		am.setSubtitleLocKey(in.getSubTitleLocKey());
//		
//		List<String> subTitleLocArgs = in.getSubTitleLocArgs();
//		if(subTitleLocArgs != null && !subTitleLocArgs.isEmpty()) 
//			for(String arg : subTitleLocArgs)
//				am.addSubtitleLocArgs(arg);
//		
//		am.setSummaryArg(in.getSummaryArg());
//		
//		if(in.getSummaryArgCount() != null)
//			am.setSummaryArgCount(in.getSummaryArgCount());
		
		return am;
	}
	
//	private static APNPayload.Sound getSound(DictionarySoundIn in) {
//		APNPayload.Sound sound = new APNPayload.Sound();
//		if(in.getCritical() != null)
//			sound.setCritical(in.getCritical());
//		
//		sound.setName(in.getName());
//		
//		if(in.getVolume() != null)
//			sound.setVolume(in.getVolume());
//		
//		return sound;
//	}
//	
//	private static List<MultiMedia> getMultiMedias(List<MultiMediaIn> ins) {
//		List<MultiMedia> mms = new ArrayList<>();
//		
//		if(ins != null) {
//			for(MultiMediaIn in : ins) {
//				MultiMedia multiMedia = new MultiMedia();
//				
//				multiMedia.setResId(in.getResId());
//				multiMedia.setResUrl(in.getResUrl());
//				
//				if(MultiMedia.MediaType.pic.getValue() == in.getResType())
//					multiMedia.setResType(MultiMedia.MediaType.pic);
//				else if(MultiMedia.MediaType.audio.getValue() == in.getResType())
//					multiMedia.setResType(MultiMedia.MediaType.audio);
//				else if(MultiMedia.MediaType.video.getValue() == in.getResType())
//					multiMedia.setResType(MultiMedia.MediaType.video);
//				
//				multiMedia.setOnlyWifi(in.getIsOnlyWifi());
//				
//				mms.add(multiMedia);
//			}
//		}
//		return mms;
//	}
	
}
