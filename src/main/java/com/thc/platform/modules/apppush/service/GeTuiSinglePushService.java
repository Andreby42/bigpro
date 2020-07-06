package com.thc.platform.modules.apppush.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gexin.fastjson.JSON;
import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.base.payload.APNPayload;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.AbstractTemplate;
import com.gexin.rp.sdk.template.NotificationTemplate;
import com.gexin.rp.sdk.template.StartActivityTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.gexin.rp.sdk.template.style.Style0;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.apppush.dto.AndroidPushIn;
import com.thc.platform.modules.apppush.dto.ExtPlatformPushRes;
import com.thc.platform.modules.apppush.dto.IosTransmissionWithApnIn;
import com.thc.platform.modules.apppush.dto.TransmissionIn;
import com.thc.platform.modules.apppush.dto.android.NotifyStyleIn;
import com.thc.platform.modules.apppush.dto.android.NotifyTemplateIn;
import com.thc.platform.modules.apppush.dto.ios.DictionaryAlertIn;
import com.thc.platform.modules.apppush.dto.ios.IosTemplateIn;
import com.thc.platform.modules.apppush.dto.ios.PayloadIn;
import com.thc.platform.modules.apppush.entity.AppGeTuiConfigEntity;
import com.thc.platform.modules.apppush.util.GTUtil;

@Service
public class GeTuiSinglePushService {

	private static String url = "http://sdk.open.api.igexin.com/apiex.htm";
	
	private static Map<String, String> TG_ERR_MAP = new HashMap<>();
	
	static {
		TG_ERR_MAP.put("sign_error", "sign鉴权失败");
		TG_ERR_MAP.put("AppidNoMatchAppKey", "Appid和AppKey不匹配");
		TG_ERR_MAP.put("PushTotalNumOverLimit", "推送总量超限");
		TG_ERR_MAP.put("TokenMD5NoUsers", "cid没有查询到相关用户");
		TG_ERR_MAP.put("flow_exceeded", "流控");
		TG_ERR_MAP.put("TooFrequent", "频控");
		TG_ERR_MAP.put("AppidError", "用户填写appId有误");
		TG_ERR_MAP.put("transmissionContent length overlimit", "透传内容长度限制");
	}
	
	@Autowired
	private AppGeTuiConfigService appGeTuiConfigService;
	
	public ExtPlatformPushRes pushTransmission(TransmissionIn in) {
		AppGeTuiConfigEntity config = appGeTuiConfigService.getById(in.getAppId());
		if(config == null)
			throw BEUtil.failNormal("个推应用配置信息不存在");
		
		String gtAppId = config.getGtAppId();
		String gtAppKey = config.getGtAppKey();
		// 个推 masterSecret
		String gtMasterSecret = config.getGtMasterSecret();
		
		SingleMessage message = new SingleMessage();
		
		message.setOffline(true);
		// 离线有效时间，单位为毫秒
		message.setOfflineExpireTime(24 * 3600 * 1000l);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        
    	TransmissionTemplate transmission = new TransmissionTemplate();

    	transmission.setAppId(gtAppId);
		transmission.setAppkey(gtAppKey);
		transmission.setTransmissionType(GTUtil.TYPE_CUSTOM_PROCESS);
		
		Map<String,Object> transmissionContent = in.getTransmissionContent();
		if(transmissionContent != null && !transmissionContent.isEmpty())
			transmission.setTransmissionContent(JSON.toJSONString(transmissionContent));
		
		message.setData(transmission);
		
		Target target = GTUtil.getTarget(in.getTarget(), gtAppId);
		
		IGtPush push = new IGtPush(url, gtAppKey, gtMasterSecret);
		IPushResult ret = push.pushMessageToSingle(message, target);
		
		ExtPlatformPushRes res = new ExtPlatformPushRes();
		Map<String, Object> gtRes = ret.getResponse();
		
		String result = (String)gtRes.get("result");
		if("ok".equals(result)) 
			res.setSuccess(true);
		else if("sign_error".equals(result))
			res.setNotes(getTgErrorMsg(result));
		
		res.setData(JSON.toJSONString(gtRes));
		
		return res;
	}
	
	public ExtPlatformPushRes pushIos(IosTransmissionWithApnIn in) {
		AppGeTuiConfigEntity config = appGeTuiConfigService.getById(in.getAppId());
		if(config == null)
			throw BEUtil.failNormal("个推应用配置信息不存在");
		
		String gtAppId = config.getGtAppId();
		String gtAppKey = config.getGtAppKey();
		// 个推 masterSecret
		String gtMasterSecret = config.getGtMasterSecret();
		
		SingleMessage message = new SingleMessage();
		
		message.setOffline(true);
		// 离线有效时间，单位为毫秒
		message.setOfflineExpireTime(24 * 3600 * 1000l);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        
        IosTemplateIn templateIn = in.getTemplate();
        
		TransmissionTemplate transmission = new TransmissionTemplate();

    	transmission.setAppId(gtAppId);
		transmission.setAppkey(gtAppKey);
		transmission.setTransmissionType(GTUtil.TYPE_CUSTOM_PROCESS);
		
		Map<String,Object> transmissionContent = templateIn.getTransmissionContent();
		if(transmissionContent != null && !transmissionContent.isEmpty())
			transmission.setTransmissionContent(JSON.toJSONString(transmissionContent));
		
		PayloadIn payloadIn = templateIn.getPayload();
		if(payloadIn != null) {
			// 添加apnPayload
			APNPayload apnPayload = new APNPayload();
			
			DictionaryAlertIn alertIn = templateIn.getPayload().getDictionaryAlert();
			if(alertIn != null) {
				APNPayload.DictionaryAlertMsg am = new APNPayload.DictionaryAlertMsg();
				am.setTitle(alertIn.getTitle());
				am.setBody(alertIn.getBody());
				apnPayload.setAlertMsg(am);
			}
			
			Map<String, Object> customMsg = templateIn.getPayload().getCustomMsg();
			if(customMsg != null && !customMsg.isEmpty()) {
				for(Map.Entry<String, Object> entry : customMsg.entrySet())
					apnPayload.addCustomMsg(entry.getKey(), JSON.toJSONString(entry.getValue()));
			} else if(transmissionContent != null && !transmissionContent.isEmpty()) {
				for(Map.Entry<String, Object> entry : transmissionContent.entrySet())
					apnPayload.addCustomMsg(entry.getKey(), JSON.toJSONString(entry.getValue()));
			}
			transmission.setAPNInfo(apnPayload);
		}
		message.setData(transmission);
		
		Target target = GTUtil.getTarget(in.getTarget(), gtAppId);
		
		IGtPush push = new IGtPush(url, gtAppKey, gtMasterSecret);
		IPushResult ret = push.pushMessageToSingle(message, target);
		
		ExtPlatformPushRes res = new ExtPlatformPushRes();
		Map<String, Object> gtRes = ret.getResponse();
		
		String result = (String)gtRes.get("result");
		if("ok".equals(result)) 
			res.setSuccess(true);
		else if("sign_error".equals(result))
			res.setNotes(getTgErrorMsg(result));
		
		res.setData(JSON.toJSONString(gtRes));
		
		return res;
	}
	
	public ExtPlatformPushRes pushAndroid(AndroidPushIn in) throws Exception {
		AppGeTuiConfigEntity config = appGeTuiConfigService.getById(in.getAppId());
		if(config == null)
			throw BEUtil.failNormal("个推应用配置信息不存在");
		
		String gtAppId = config.getGtAppId();
		String gtAppKey = config.getGtAppKey();
		// 个推 masterSecret
		String gtMasterSecret = config.getGtMasterSecret();
		
		SingleMessage message = new SingleMessage();
		
		message.setOffline(true);
		// 离线有效时间，单位为毫秒
		message.setOfflineExpireTime(24 * 3600 * 1000l);
		// 可选，1为wifi，0为不限制网络环境。根据手机处于的网络情况，决定是否下发
        message.setPushNetWorkType(0);
        
        NotifyTemplateIn templateIn = in.getTemplate();
        
        AbstractTemplate template = null;
        String intent = templateIn.getIntent();
        if(StringUtil.isNotEmpty(intent)) {
        	StartActivityTemplate t = new StartActivityTemplate();
     		t.setAppId(gtAppId);
     		t.setAppkey(gtAppKey);
     		t.setIntent(intent); 
     		
    		NotifyStyleIn styleIn = templateIn.getStyle();
    		if(styleIn != null) {
    			Style0 s = new Style0();
    			s.setTitle(styleIn.getTitle());
    			s.setText(styleIn.getText());
    			t.setStyle(s);
    		}
    		
     		template = t;
        } else {
        	NotificationTemplate t = new NotificationTemplate();
    		t.setAppId(gtAppId);
    		t.setAppkey(gtAppKey);
    		t.setTransmissionType(GTUtil.TYPE_CUSTOM_PROCESS);
    		
    		Map<String,Object> transmissionContent = templateIn.getTransmissionContent();
    		if(transmissionContent != null && !transmissionContent.isEmpty())
    			t.setTransmissionContent(JSON.toJSONString(transmissionContent));
    		
    		NotifyStyleIn styleIn = templateIn.getStyle();
    		if(styleIn != null) {
    			Style0 s = new Style0();
    			s.setTitle(styleIn.getTitle());
    			s.setText(styleIn.getText());
    			t.setStyle(s);
    		}
    		
    		template = t;
        }
		
		message.setData(template);
		
		Target target = GTUtil.getTarget(in.getTarget(), gtAppId);
		
		IGtPush push = new IGtPush(url, gtAppKey, gtMasterSecret);
		IPushResult ret = push.pushMessageToSingle(message, target);
		
		ExtPlatformPushRes res = new ExtPlatformPushRes();
		Map<String, Object> gtRes = ret.getResponse();
		
		String result = (String)gtRes.get("result");
		if("ok".equals(result)) 
			res.setSuccess(true);
		else if("sign_error".equals(result))
			res.setNotes(getTgErrorMsg(result));
		
		res.setData(JSON.toJSONString(gtRes));
		
		return res;
	}
	
//	public void pushSingle(BatchSinglePushIn in) throws Exception {
//		AppGeTuiConfigEntity config = appGeTuiConfigService.getById(in.getAppId());
//		if(config == null)
//			throw BEUtil.failNormal("个推配置信息不存在");
//		
//		String gtAppId = config.getGtAppId();
//		String gtAppKey = config.getGtAppKey();
//		// 个推 masterSecret
//		String gtMasterSecret = config.getGtMasterSecret();
//		
//		IGtPush push = new IGtPush(url, gtAppKey, gtMasterSecret);
//		IBatch batch = push.getBatch();
//		
//		for(CustomPush item : in.getItems()) {
//			TargetIn targetIn = item.getTarget();
//			
//			Target target = new Target();
//	        target.setAppId(gtAppId);
//	        target.setClientId(targetIn.getClientId());
//	        target.setAlias(targetIn.getAlias());
//	        
//	        SingleMessage message = new SingleMessage();
//	        
//	        if(in.getIsOffline() != null) 
//				message.setOffline(in.getIsOffline());
//				
//	        if(in.getOfflineExpireTime() != null)
//				message.setOfflineExpireTime(in.getOfflineExpireTime());
//	        
//	        if(in.getPushNetWorkType() != null)
//	        	message.setPushNetWorkType(in.getPushNetWorkType());
//	        
//			/** Android 内容模版 */
//			// 通知
//			NotifyTemplateIn notify = item.getNotify();
//			// 透传
//			TransmissionTemplateIn transmission = item.getTransmission();
//			
//			/** IOS 内容模版 */
//			IosTemplateIn iosIn = item.getIos();
//			if(iosIn != null) {
//				TransmissionTemplate t = GTIosUtil.getTemplate(iosIn, gtAppId, gtAppKey);
//				message.setData(t);
//			} else if(transmission != null) {
//				TransmissionTemplate t = GTAndroidUtil.getTransmissionTemplate(transmission, gtAppId, gtAppKey);
//				message.setData(t);
//			} else if(notify != null) {
//				AbstractTemplate t = GTAndroidUtil.getNotifyTemplate(notify, gtAppId, gtAppKey);
//				message.setData(t);
//			}
//			
//			batch.add(message, target);
//		}
//
////		IPushResult ret = batch.submit();
//		
//	}
//	
	private String getTgErrorMsg(String result) {
		String msg = TG_ERR_MAP.get(result);
		if(msg != null)
			return msg;
		
		if(result.indexOf("is not null") > 0)
			return "参数校验失败";
		
		if(result.indexOf("Text or Title Contain Sensitive Word Error") > 0)
			return "敏感词受限";
		
		return "其他错误：" + result;
	}
	
}
