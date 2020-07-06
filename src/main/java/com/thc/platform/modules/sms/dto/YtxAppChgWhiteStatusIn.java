package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class YtxAppChgWhiteStatusIn {

	// ID
	private String id;
	// 白名单状态
	private Integer whiteListStatus;
	
	public void validate() {
		if (StringUtil.isEmpty(id))
			throw BEUtil.illegalFormat("name is empty");
		
		if(whiteListStatus == null)
			throw BEUtil.illegalFormat("whiteListStatus is null");
		
		if(whiteListStatus != 1)
			whiteListStatus = 0;
	}
}
