package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.dto.PageIn;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class YtxTemplateListIn extends PageIn {

	// 应用ID
	private String appId;
	// 荣联模版ID
	private String templateId;
	// 模版类型
	private Integer type;
	// 模版签名ID
	private String signTypeId;
	
	public void validate() {
		super.validate();
	}
}
