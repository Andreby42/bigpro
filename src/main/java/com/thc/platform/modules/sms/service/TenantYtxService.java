package com.thc.platform.modules.sms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.sms.dto.CCPRestInvokeInfo;
import com.thc.platform.modules.sms.entity.YtxAccountEntity;
import com.thc.platform.modules.sms.entity.YtxAppEntity;
import com.thc.platform.modules.sms.entity.YtxTemplateEntity;

import lombok.Data;

/** 租户荣联云通讯服务 */
@Service
public class TenantYtxService {

	// 过期毫秒数
	private static final long EXPIRE_INTERVAL_MILLIS = 1000l * 60;
	// 云通讯状态：成功
	private static final String YTX_STATUS_CODE_SUCCESS = "000000";
	// 云通讯状态：重复
	private static final String YTX_STATUS_CODE_DUPLICATE = "160054";
	
	private Logger logger = LoggerFactory.getLogger(TenantYtxService.class);
	
	@Autowired
	private TenantYtxAppRelService tenantYtxAppRelService;
	@Autowired
	private YtxAccountService ytxAccountService;
	@Autowired
	private YtxAppService ytxAppService;
	@Autowired
	private YtxTemplateService ytxTemplateService;
	@Autowired
	private WhiteNumService whiteNumService;
	
	private Map<String, YtxAccountSdk> ytxAccountSdkMap = new HashMap<>();
	private Map<Integer, YtxAppInfo> tenantYtxApiMap = new HashMap<>();
	
	/**
	 * 荣联云通讯短信发送功能
	 * @param tenantId 租户ID
	 * @param type 短信类型
	 * @param signTypeId 短信签名类型
	 * @param mobile 手机号
	 * @param content 发送内容
	 * @return
	 */
	public CCPRestInvokeInfo sendSMS(Integer tenantId, int type, String signTypeId, String mobiles, String content, String reqId) {
		YtxAppInfo appInfo = getYtxAppInfo(tenantId);
		YtxAppEntity ytxApp = appInfo.getYtxApp();
		String templateId = appInfo.getTemplateId(type, signTypeId);
		YtxAccountSdk sdk = getYtxAccountSdk(ytxApp.getAccountId());
		
		if(appInfo.isWhiteListSwitchOff()) {
			CCPRestInvokeInfo invokeInfo = sdk.send(ytxApp.getAppId(), reqId, mobiles, templateId, content);
			invokeInfo.setTemplateId(templateId);
			return invokeInfo;
		}
		StringBuilder wlSb = new StringBuilder();
		StringBuilder blSb = new StringBuilder();
		int wlCount = 0;
		int blCount = 0;
		for(String mobile : mobiles.split(",")) {
			if(whiteNumService.countByBusiId(ytxApp.getId(), mobile) > 0) {
				if(wlCount > 0)
					wlSb.append(",");
				
				wlSb.append(mobile);
				wlCount ++;
			} else {
				if(blCount > 0)
					blSb.append(",");
				
				blSb.append(mobile);
				blCount ++;
			}
		}
		
		String wlMobiles = wlSb.toString();
		String blMobiles = blSb.toString();
		
		CCPRestInvokeInfo invokeInfo = null;
		
		if(StringUtil.isNotEmpty(wlMobiles)) {
			invokeInfo = sdk.send(ytxApp.getAppId(), reqId, wlMobiles, templateId, content);
			invokeInfo.setTemplateId(templateId);
		} else {
			invokeInfo = new CCPRestInvokeInfo();
		}
		if(StringUtil.isNotEmpty(blMobiles))
			invokeInfo.setNotes("not in white list mobiles: " + blSb.toString());
		
		return invokeInfo;
	}
	
	@Data
	public class YtxAccountSdk {
		
		private YtxAccountEntity account;
		private long loadMillis = System.currentTimeMillis();
		private SmsCCPRestSDK sdk;

		public YtxAccountSdk(YtxAccountEntity account) {
			sdk = new SmsCCPRestSDK();
			sdk.init(account.getUrl(), account.getPort());
			sdk.setAccount(account.getAccountSid(), account.getAuthToken());
		}

		public boolean isExpired() {
			return (System.currentTimeMillis() - loadMillis) > EXPIRE_INTERVAL_MILLIS;
		}
		
		public CCPRestInvokeInfo send(String appId
				, String reqId
				, String to
				, String templateId
				, String content) {
			
			CCPRestInvokeInfo invokeInfo = sdk.sendTemplateSMS(appId, reqId, to, templateId, new String[]{content});
			
			if(invokeInfo.isInvokeSuccess()) {
		    	try {
					JSONObject jsonObj = JSON.parseObject(invokeInfo.getResData());
					String statusCode = jsonObj.getString("statusCode");
					String statusMsg = jsonObj.getString("statusMsg");
					
					if(YTX_STATUS_CODE_SUCCESS.equals(statusCode)
							|| YTX_STATUS_CODE_DUPLICATE.equals(statusCode)) {
						invokeInfo.busiSuccess(statusCode);
						int feeNum = calculateFeeNum(content);
						invokeInfo.setFeeNum(feeNum);
					} else {
						invokeInfo.busiFail(statusCode, statusMsg);
					}
				} catch (Exception e) {
					String errMsg = "解析返回结果错误。";
					logger.error(errMsg, e);
					invokeInfo.setBusiSuccess(false);
					invokeInfo.setNotes(errMsg + e.getMessage());
				}
		    }
			
			return invokeInfo;
		}
	}
	
	private static int calculateFeeNum(String content) {
		int feeNum = 1;
		int contentLength = content.length();
		if (contentLength > 70) {
			feeNum = contentLength / 67;
			if (contentLength % 67 > 0)
				feeNum++;
		}
		return feeNum;
	}
	
	private YtxAccountSdk getYtxAccountSdk(String ytxAccountId) {
		YtxAccountSdk sdk = ytxAccountSdkMap.get(ytxAccountId);
		if(sdk == null || sdk.isExpired()) {
			YtxAccountEntity account = ytxAccountService.getById(ytxAccountId);
			if(account == null)
				throw BEUtil.failCauseConfigData("短信通道配置错误：未找到相关荣联账户");
			
			sdk = new YtxAccountSdk(account);
			ytxAccountSdkMap.put(account.getId(), sdk);
		}
		
		return sdk;
	}
	
	private YtxAppInfo getYtxAppInfo(Integer tenantId) {
		YtxAppInfo appInfo = tenantYtxApiMap.get(tenantId);
		if(appInfo == null || appInfo.isExpired()) {
			String appId = tenantYtxAppRelService.getAppId(tenantId);
			YtxAppEntity app = ytxAppService.getById(appId);
			if(app == null)
				throw BEUtil.failCauseConfigData("短信通道配置错误：未找到相关荣联应用信息");
			
			TenantYtxTemplate tenantYtxTemplate = loadTenantYtxTemplate(app.getId());
			
			appInfo = new YtxAppInfo(app, tenantYtxTemplate);
			tenantYtxApiMap.put(tenantId, appInfo);
		}
		return appInfo;
	}
	
	private TenantYtxTemplate loadTenantYtxTemplate(String appId) {
		
		TenantYtxTemplate tenantYtxTemplate = new TenantYtxTemplate();
		
		List<YtxTemplateEntity> ytxTemplates = ytxTemplateService.getByAppId(appId);
		if(ytxTemplates.size() == 0)
			throw BEUtil.failCauseConfigData("租户关联应用未配置相关模版信息");
		
		for(YtxTemplateEntity ytxTemplate : ytxTemplates) {
			if(ytxTemplate != null)
				tenantYtxTemplate.add(ytxTemplate.getSignTypeId(), ytxTemplate.getType(), ytxTemplate.getTemplateId());
		}
		return tenantYtxTemplate;
	}
	
	public static class TenantYtxTemplate {
		
		private Random random = new Random();
		
		public Map<String, List<String>> verifyMap = new HashMap<>();
		public Map<String, List<String>> noticeMap = new HashMap<>();
		
		public String getTemplateId(int type, String signTypeId) {
			if(type == YtxTemplateEntity.TYPE_VERIFY)
				return getVerifyTemplateId(signTypeId);
			else if(type == YtxTemplateEntity.TYPE_NOTICE)
				return getNoticeTemplateId(signTypeId);
			
			return null;
		}
		
		private String getVerifyTemplateId(String signTypeId) {
			List<String> templateIds = verifyMap.get(signTypeId);
			if(templateIds == null || templateIds.size() == 0)
				return null;
			
			int randomIndex = random.nextInt(templateIds.size());
			return templateIds.get(randomIndex);
		}
		
		private String getNoticeTemplateId(String signTypeId) {
			List<String> templateIds = noticeMap.get(signTypeId);
			if(templateIds == null || templateIds.size() == 0)
				return null;
			
			int randomIndex = random.nextInt(templateIds.size());
			return templateIds.get(randomIndex);
		}
		
		public void add(String signTypeId, Integer templateType, String templateId) {
			if(YtxTemplateEntity.TYPE_VERIFY == templateType)
				addVerify(signTypeId, templateId);
			else
				addNotice(signTypeId, templateId);
		}
		
		private void addVerify(String signTypeId, String templateId) {
			List<String> templateIds = verifyMap.get(signTypeId);
			if(templateIds == null) {
				templateIds = new ArrayList<>();
				verifyMap.put(signTypeId, templateIds);
			}
			templateIds.add(templateId);
		}
		
		private void addNotice(String signTypeId, String templateId) {
			List<String> templateIds = noticeMap.get(signTypeId);
			if(templateIds == null) {
				templateIds = new ArrayList<>();
				noticeMap.put(signTypeId, templateIds);
			}
			templateIds.add(templateId);
		}

	}
	
	@Data
	public static class YtxResult {
		
		private static final String SUCCESS = "000000";
		
		private String code;
		private String resData;
		private String templateId;
		private String notes;
		private int feeCount;
		
		public YtxResult(String code, String resData, String templateId, int feeCount) {
			this.code = code;
			this.resData = resData;
			this.templateId = templateId;
			this.feeCount = feeCount;
		}

		public boolean isSuccess() {
			return SUCCESS.equals(code);
		}
		
	}
	
	public class YtxAppInfo {

		private long loadMillis = System.currentTimeMillis();
		private TenantYtxTemplate tenantYtxTemplate;
		private boolean whiteListSwitchOn = false;
		
		private YtxAppEntity app;
		
		public YtxAppInfo(YtxAppEntity app, TenantYtxTemplate tenantYtxTemplate) {
			whiteListSwitchOn = app.getWhiteListStatus() == YtxAppEntity.WHITE_LIST_STATUS_ON;
			this.tenantYtxTemplate = tenantYtxTemplate;
			this.app = app;
		}
		
		public YtxAppEntity getYtxApp() {
			return app;
		}

		public String getTemplateId(int type, String signTypeId) {
			String templateId = tenantYtxTemplate.getTemplateId(type, signTypeId);
			if(StringUtil.isEmpty(templateId))
				throw BEUtil.failCauseConfigData("短信通道配置错误：未找到相关的荣联短信模版");
			
			return templateId;
		}
		
		public boolean isWhiteListSwitchOff() {
			return !whiteListSwitchOn;
		}
		
	    public boolean isExpired() {
	    	return (System.currentTimeMillis() - loadMillis) > EXPIRE_INTERVAL_MILLIS; 
	    }
	}

}
