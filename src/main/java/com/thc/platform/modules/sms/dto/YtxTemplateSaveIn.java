package com.thc.platform.modules.sms.dto;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.sms.entity.YtxTemplateEntity;

import lombok.Data;

@Data
public class YtxTemplateSaveIn {

	// ID
	private String id;
	// 荣联模版ID
	private String templateId;
	// 模版类型
	private Integer type;
	// 应用ID
	private String appId;
	// 模版签名ID
	private String signTypeId;
	// 备注信息
	private String notes;
	
	public void validate() {
		if (StringUtil.isEmpty(templateId))
			throw BEUtil.illegalFormat("templateId is empty");
		
		if(type == null)
			throw BEUtil.illegalFormat("type is null");
		
		if(type != YtxTemplateEntity.TYPE_VERIFY 
				&& type != YtxTemplateEntity.TYPE_NOTICE)
			throw BEUtil.illegalFormat("undefined type: " + type);
		
		if (StringUtil.isEmpty(appId))
			throw BEUtil.illegalFormat("appId is empty");
		
		if (StringUtil.isEmpty(signTypeId))
			throw BEUtil.illegalFormat("signTypeId is empty");
		
	}
	
}
