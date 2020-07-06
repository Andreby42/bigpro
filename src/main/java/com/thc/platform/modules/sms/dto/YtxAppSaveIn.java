package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class YtxAppSaveIn {

	// ID
	private String id;
	// 名称
	private String name;
	// 云通讯 App ID
	private String appId;
	// 白名单状态
	private Integer whiteListStatus;
//	// 账户ID
//	private String accountId;
	// 备注
	private String notes;
	
	public void validate() {
		if (StringUtil.isEmpty(name))
			throw BEUtil.illegalFormat("name is empty");
		
		if (StringUtil.isEmpty(appId))
			throw BEUtil.illegalFormat("appId is empty");
		
//		if (StringUtil.isEmpty(accountId))
//			throw BEUtil.illegalFormat("accountId is empty");
		
		if(whiteListStatus == null || whiteListStatus != 1)
			whiteListStatus = 0;
	}
	
}
