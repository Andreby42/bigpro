package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class YtxAccountSaveIn {

	// ID
	private String id;
	// 名称
	private String name;
	// 账户ID
	private String accountSid;
	// 应用ID
	private String authToken;
	// rest服务地址
	private String url;
	// rest服务端口号
	private String port;
	// 账号备注信息
	private String notes;
	
	public void validate() {
		if (StringUtil.isEmpty(name))
			throw BEUtil.illegalFormat("name is empty");
		
		if (StringUtil.isEmpty(accountSid))
			throw BEUtil.illegalFormat("accountSid is empty");
		
		if (StringUtil.isEmpty(authToken))
			throw BEUtil.illegalFormat("authToken is empty");
		
		if (StringUtil.isEmpty(url))
			throw BEUtil.illegalFormat("url is empty");
		
		if (StringUtil.isEmpty(port))
			throw BEUtil.illegalFormat("port is empty");
		
	}
	
}
