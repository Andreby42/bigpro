/*
 *  Copyright (c) 2014 The CCP project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a Beijing Speedtong Information Technology Co.,Ltd license
 *  that can be found in the LICENSE file in the root of the web site.
 *
 *   http://www.yuntongxun.com
 *
 *  An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */
package com.thc.platform.modules.sms.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cloopen.rest.sdk.utils.CcopHttpClient;
import com.cloopen.rest.sdk.utils.DateUtil;
import com.cloopen.rest.sdk.utils.EncryptUtil;
import com.cloopen.rest.sdk.utils.LoggerUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.modules.sms.dto.CCPRestInvokeInfo;

import ytx.org.apache.http.HttpEntity;
import ytx.org.apache.http.HttpResponse;
import ytx.org.apache.http.client.methods.HttpGet;
import ytx.org.apache.http.client.methods.HttpPost;
import ytx.org.apache.http.client.methods.HttpRequestBase;
import ytx.org.apache.http.entity.BasicHttpEntity;
import ytx.org.apache.http.impl.client.DefaultHttpClient;
import ytx.org.apache.http.message.AbstractHttpMessage;
import ytx.org.apache.http.util.EntityUtils;

public class SmsCCPRestSDK {
	
	private static final int Request_Get = 0;

	private static final int Request_Post = 1;
	private static final String TemplateSMS = "SMS/TemplateSMS";
	
	private Logger logger = LoggerFactory.getLogger(SmsCCPRestSDK.class);
	
	private String SERVER_IP;
	private String SERVER_PORT;
	private String ACCOUNT_SID;
	private String ACCOUNT_TOKEN;
	private String SUBACCOUNT_SID;
	private String SUBACCOUNT_Token;
	
			
//	public enum BodyType {
//		Type_XML, Type_JSON;
//	}

	public enum AccountType {
		Accounts, SubAccounts;
	}

	/**
	 * 初始化服务地址和端口
	 * 
	 * @param serverIP
	 *            必选参数 服务器地址
	 * @param serverPort
	 *            必选参数 服务器端口
	 */
	public void init(String serverIP, String serverPort) {
		if (isEmpty(serverIP) || isEmpty(serverPort)) {
			LoggerUtil.fatal("初始化异常:serverIP或serverPort为空");
			throw new IllegalArgumentException("必选参数:"
					+ (isEmpty(serverIP) ? " 服务器地址 " : "")
					+ (isEmpty(serverPort) ? " 服务器端口 " : "") + "为空");
		}
		SERVER_IP = serverIP;
		SERVER_PORT = serverPort;
	}

	/**
	 * 初始化主帐号信息
	 * 
	 * @param accountSid
	 *            必选参数 主帐号
	 * @param accountToken
	 *            必选参数 主帐号TOKEN
	 */
	public void setAccount(String accountSid, String accountToken) {
		if (isEmpty(accountSid) || isEmpty(accountToken)) {
			LoggerUtil.fatal("初始化异常:accountSid或accountToken为空");
			throw new IllegalArgumentException("必选参数:"
					+ (isEmpty(accountSid) ? " 主帐号" : "")
					+ (isEmpty(accountToken) ? " 主帐号TOKEN " : "") + "为空");
		}
		ACCOUNT_SID = accountSid;
		ACCOUNT_TOKEN = accountToken;
	}

	/**
	 * 初始化子帐号信息
	 * 
	 * @param subAccountSid
	 *            必选参数 子帐号
	 * @param subAccountToken
	 *            必选参数 子帐号TOKEN
	 */
	public void setSubAccount(String subAccountSid, String subAccountToken) {
		if (isEmpty(subAccountSid) || isEmpty(subAccountToken)) {
			LoggerUtil.fatal("初始化异常:subAccountSid或subAccountToken为空");
			throw new IllegalArgumentException("必选参数:"
					+ (isEmpty(subAccountSid) ? " 子帐号" : "")
					+ (isEmpty(subAccountToken) ? " 子帐号TOKEN " : "") + "为空");
		}
		SUBACCOUNT_SID = subAccountSid;
		SUBACCOUNT_Token = subAccountToken;
	}

	/**
	 * 初始化应用Id
	 * 
	 * @param appId
	 *            必选参数 应用Id
	 */
//	public void setAppId(String appId) {
//		if (isEmpty(appId)) {
//			LoggerUtil.fatal("初始化异常:appId为空");
//			throw new IllegalArgumentException("必选参数: 应用Id 为空");
//		}
//		App_ID = appId;
//	}

	/**
	 * 发送短信模板请求
	 * 
	 * @param to
	 *            必选参数 短信接收端手机号码集合，用英文逗号分开，每批发送的手机号数量不得超过100个
	 * @param templateId
	 *            必选参数 模板Id
	 * @param datas
	 *            可选参数 内容数据，用于替换模板中{序号}
	 * @return
	 */
	public CCPRestInvokeInfo sendTemplateSMS(
			String appId
			, String reqId
			, String to
			, String templateId
			, String[] datas) {
		
		CCPRestInvokeInfo restInvokeInfo = new CCPRestInvokeInfo();
		
		String validate = accountValidate();
		if (validate != null)
			return restInvokeInfo.fail(validate);
		
		if ((isEmpty(to)) || (isEmpty(appId)) || (isEmpty(templateId)))
			return restInvokeInfo.fail("必选参数:"
					+ (isEmpty(to) ? " 手机号码 " : "")
					+ (isEmpty(templateId) ? " 模板Id " : "") + "为空");
		
		CcopHttpClient chc = new CcopHttpClient();
		DefaultHttpClient httpclient = null;
		try {
			httpclient = chc.registerSSL(SERVER_IP, "TLS",
					Integer.parseInt(SERVER_PORT), "https");
		} catch (Exception e) {
			String errMsg = "初始化httpclient异常";
			logger.error(errMsg, e);
			return restInvokeInfo.fail(errMsg + ": " + e.getMessage());
		}
		
		String result = "";
		try {
			HttpPost httppost = (HttpPost) getHttpRequestBase(1, TemplateSMS);
			String requsetbody = "";
			JsonObject json = new JsonObject();
			json.addProperty("appId", appId);
			json.addProperty("reqId", reqId);
			json.addProperty("to", to);
			json.addProperty("templateId", templateId);
			if (datas != null) {
				StringBuilder sb = new StringBuilder("[");
				for (String s : datas) {
					sb.append("\"" + s + "\"" + ",");
				}
				sb.replace(sb.length() - 1, sb.length(), "]");
				JsonParser parser = new JsonParser();
				JsonArray Jarray = parser.parse(sb.toString())
						.getAsJsonArray();
				json.add("datas", Jarray);
			}
			requsetbody = json.toString();
			// 设置请求数据
			restInvokeInfo.setReqData(requsetbody);
			
			BasicHttpEntity requestBody = new BasicHttpEntity();
			requestBody.setContent(new ByteArrayInputStream(requsetbody.getBytes("UTF-8")));
			requestBody.setContentLength(requsetbody.getBytes("UTF-8").length);
			httppost.setEntity(requestBody);
			
			HttpResponse response = httpclient.execute(httppost);
			
			
			//获取响应码
			int httpResCode = response.getStatusLine().getStatusCode();
			restInvokeInfo.setHttpResCode(httpResCode);

			HttpEntity entity = response.getEntity();
			if (entity != null)
				result = EntityUtils.toString(entity, "UTF-8");

			EntityUtils.consume(entity);
		} catch (IOException e) {
			String errMsg = "网络错误。";
			logger.error(errMsg, e);
			return restInvokeInfo.fail(errMsg + e.getMessage());
		} catch (Exception e) {
			String errMsg = "无返回。";
			logger.error(errMsg, e);
			return restInvokeInfo.fail(errMsg + e.getMessage());
		} finally {
			if (httpclient != null)
				httpclient.getConnectionManager().shutdown();
		}

		return restInvokeInfo.success(result);
	}

	private HttpRequestBase getHttpRequestBase(int get, String action)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return getHttpRequestBase(get, action, AccountType.Accounts);
	}

	private HttpRequestBase getHttpRequestBase(int get, String action, AccountType mAccountType) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		
		String timestamp = DateUtil.dateToStr(new Date(), DateUtil.DATE_TIME_NO_SLASH);
		
		EncryptUtil eu = new EncryptUtil();
		String sig = "";
		String acountName = "";
		String acountType = "";
		if (mAccountType == AccountType.Accounts) {
			acountName = ACCOUNT_SID;
			sig = ACCOUNT_SID + ACCOUNT_TOKEN + timestamp;
			acountType = "Accounts";
		} else {
			acountName = SUBACCOUNT_SID;
			sig = SUBACCOUNT_SID + SUBACCOUNT_Token + timestamp;
			acountType = "SubAccounts";
		}
		String signature = eu.md5Digest(sig);

		String url = getBaseUrl().append("/" + acountType + "/")
				.append(acountName).append("/" + action + "?sig=")
				.append(signature).toString();

		HttpRequestBase mHttpRequestBase = null;
		if (get == Request_Get)
			mHttpRequestBase = new HttpGet(url);
		else if (get == Request_Post)
			mHttpRequestBase = new HttpPost(url);
		else
			throw BEUtil.failNormal("unexpected method");
		
		setHttpHeader(mHttpRequestBase);

		String src = acountName + ":" + timestamp;

		String auth = eu.base64Encoder(src);
		mHttpRequestBase.setHeader("Authorization", auth);
//		System.out.println("请求的Url："+mHttpRequestBase);//打印Url
		return mHttpRequestBase;
		
	}

	private void setHttpHeader(AbstractHttpMessage httpMessage) {
		httpMessage.setHeader("Accept", "application/json");
		httpMessage.setHeader("Content-Type",
				"application/json;charset=utf-8");
	}

	private StringBuffer getBaseUrl() {
		StringBuffer sb = new StringBuffer("https://");
		sb.append(SERVER_IP).append(":").append(SERVER_PORT);
		sb.append("/2013-12-26");
		return sb;
	}

	private boolean isEmpty(String str) {
		return (("".equals(str)) || (str == null));
	}

	private String accountValidate() {
		if ((isEmpty(SERVER_IP)))
			return "IP为空";
		
		if ((isEmpty(SERVER_PORT))) 
			return "端口错误";
		
		if ((isEmpty(ACCOUNT_SID))) 
			return "主帐号为空";
		
		if ((isEmpty(ACCOUNT_TOKEN))) 
			return "主帐号TOKEN为空";
		
		return null;
	}

}