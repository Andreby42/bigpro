package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.sms.constant.SmsType;

import lombok.Data;

@Data
public class SysTemplateSaveIn {

	private String id;
	// 编码
	private String code;
	// 模版类型
	private Integer smsType;
	// 名称
	private String name;
	// 内容 带参数
	private String content;
	
	public void validate() {
		if (StringUtil.isEmpty(code))
			throw BEUtil.illegalFormat("code is empty");
		
		if (smsType == null)
			throw BEUtil.illegalFormat("smsType is null");
		
		if(smsType != SmsType.VERIFY_CODE 
				&& smsType != SmsType.NOTICE)
			throw BEUtil.illegalFormat("undefined smsType: " + smsType);
			
		if (StringUtil.isEmpty(name))
			throw BEUtil.illegalFormat("name is empty");
		
		if (StringUtil.isEmpty(content))
			throw BEUtil.illegalFormat("content is empty");
		
	}
	
}
