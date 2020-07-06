package com.thc.platform.modules.apppush.service;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gexin.fastjson.JSON;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.SerialNumUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.modules.apppush.dto.AndroidPushIn;
import com.thc.platform.modules.apppush.dto.ExtPlatformPushRes;
import com.thc.platform.modules.apppush.dto.IosTransmissionWithApnIn;
import com.thc.platform.modules.apppush.dto.TransmissionIn;
import com.thc.platform.modules.apppush.entity.AppPushRecordEntity;

@Service
public class PushService {

	private final Logger logger = LoggerFactory.getLogger(PushService.class);
	
//	public static final String PUSH_TYPE_TRANSMISSION = "transmission";
//	public static final String PUSH_TYPE_IOS = "ios";
//	public static final String PUSH_TYPE_ANDROID = "android";
	
	@Autowired
	private GeTuiSinglePushService geTuiSinglePushService;
	@Autowired
	private AppPushRecordService appPushRecordService;
	
//	public void pushSingle(SinglePushIn in) {
//		Date currentTime = new Date();
//		String reqSerialNum = SerialNumUtil.serialNum(currentTime);
////		if(in.getNotify() == null) {
////			pushSingle(reqSerialNum, in, currentTime, PUSH_TYPE_TRANSMISSION);
////		} else {
////			if(in.getPlatform() == SinglePushIn.PLATFORM_ALL) {
////				pushSingle(reqSerialNum, in, currentTime, PUSH_TYPE_IOS);
////				pushSingle(reqSerialNum, in, currentTime, PUSH_TYPE_ANDROID);
////			} else if(in.getPlatform() == SinglePushIn.PLATFORM_IOS) {
////				pushSingle(reqSerialNum, in, currentTime, PUSH_TYPE_IOS);
////			} else if(in.getPlatform() == SinglePushIn.PLATFORM_ANDROID) {
////				pushSingle(reqSerialNum, in, currentTime, PUSH_TYPE_ANDROID);
////			}
////		}
//		
//	}
	
	public void pushSingle(Object in) {
		Date currentTime = new Date();
		String reqSerialNum = SerialNumUtil.serialNum(currentTime);
		
		AppPushRecordEntity record = new AppPushRecordEntity();
		
		record.setId(ShortUUID.uuid());
		record.setReqSerialNum(reqSerialNum);
		record.setReqData(JSON.toJSONString(in));
		
		try {
			ExtPlatformPushRes res = null;
			
			if(in instanceof IosTransmissionWithApnIn)
				res = geTuiSinglePushService.pushIos((IosTransmissionWithApnIn)in);
			else if(in instanceof AndroidPushIn)
				res = geTuiSinglePushService.pushAndroid((AndroidPushIn)in);
			else if(in instanceof TransmissionIn)
				res = geTuiSinglePushService.pushTransmission((TransmissionIn)in);
			
			if(res != null) {
				record.setExtPlatfromResData(res.getData());
				
				if(res.getSuccess()) {
					record.setStatus(AppPushRecordEntity.STATUS_SUCCESS);
				} else {
					record.setStatus(AppPushRecordEntity.STATUS_FAIL);
					record.setNotes("个推平台调用错误：" + res.getNotes());
				}
			} else {
				throw BEUtil.failNormal("返回结果为空");
			}
		} catch (Exception e) {
			logger.error("getui push error.", e);
			record.setStatus(AppPushRecordEntity.STATUS_FAIL);
			record.setNotes(e.getMessage());
		}
		
		record.setCreateTime(currentTime);
		appPushRecordService.save(record);
	}
	
//	public void pushIos(IosTransmissionWithApnIn in) {
//		Date currentTime = new Date();
//		String reqSerialNum = SerialNumUtil.serialNum(currentTime);
//		
//		AppPushRecordEntity record = new AppPushRecordEntity();
//		
//		record.setId(ShortUUID.uuid());
//		record.setReqSerialNum(reqSerialNum);
//		record.setReqData(JSON.toJSONString(in));
//		
//		try {
//			ExtPlatformPushRes res = geTuiSinglePushService.pushIos(in);
//			record.setExtPlatfromResData(res.getData());
//			if(res.getSuccess()) {
//				record.setStatus(AppPushRecordEntity.STATUS_SUCCESS);
//			} else {
//				record.setStatus(AppPushRecordEntity.STATUS_FAIL);
//				record.setNotes("个推平台调用错误：" + res.getNotes());
//			}
//		} catch (Exception e) {
//			logger.error("getui push error.", e);
//			record.setStatus(AppPushRecordEntity.STATUS_FAIL);
//			record.setNotes(e.getMessage());
//		}
//		
//		record.setCreateTime(currentTime);
//		appPushRecordService.save(record);
//	}
//	
//	public void pushAndroid(AndroidPushIn in) {
//		Date currentTime = new Date();
//		String reqSerialNum = SerialNumUtil.serialNum(currentTime);
//		
//		AppPushRecordEntity record = new AppPushRecordEntity();
//		
//		record.setId(ShortUUID.uuid());
//		record.setReqSerialNum(reqSerialNum);
//		record.setReqData(JSON.toJSONString(in));
//		
//		try {
//			ExtPlatformPushRes res = geTuiSinglePushService.pushAndroid(in);
//			record.setExtPlatfromResData(res.getData());
//			if(res.getSuccess()) {
//				record.setStatus(AppPushRecordEntity.STATUS_SUCCESS);
//			} else {
//				record.setStatus(AppPushRecordEntity.STATUS_FAIL);
//				record.setNotes("个推平台调用错误：" + res.getNotes());
//			}
//		} catch (Exception e) {
//			logger.error("getui push error.", e);
//			record.setStatus(AppPushRecordEntity.STATUS_FAIL);
//			record.setNotes(e.getMessage());
//		}
//		
//		record.setCreateTime(currentTime);
//		appPushRecordService.save(record);
//	}
}
