package com.thc.platform.modules.apppush.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class TargetIn {

	// 客户端身份ID clientId和alias二选一
	private String clientId;
	// 用户别 clientId和alias二选一
	private String alias;

	public void validate() {
		if(StringUtil.isEmpty(clientId) && StringUtil.isEmpty(alias))
			throw BEUtil.failNormal("clientId和alias二选一");
	}
	
}
