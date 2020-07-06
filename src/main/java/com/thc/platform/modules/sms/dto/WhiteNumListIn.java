package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.dto.PageIn;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class WhiteNumListIn extends PageIn {

	private String ytxAppId;
	// 手机号或者备注
	private String keyword;
	
	public void validate() {
		super.validate();
		
		if (StringUtil.isEmpty(ytxAppId))
			throw BEUtil.illegalFormat("ytxAppId is empty");
	}
}
