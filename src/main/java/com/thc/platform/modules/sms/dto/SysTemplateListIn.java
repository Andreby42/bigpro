package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.dto.PageIn;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SysTemplateListIn extends PageIn {

	// 编码
	private String code;
	// 模版类型
	private Integer smsType;
	// 名称
	private String name;
	// 租户ID
	private Integer tenantId;

	public void validate() {
		super.validate();
		
		if(StringUtil.isEmpty(code))
			code = null;
		
		if(StringUtil.isEmpty(name))
			name = null;
		
	}

}
