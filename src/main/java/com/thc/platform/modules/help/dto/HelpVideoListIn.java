package com.thc.platform.modules.help.dto;

import com.thc.platform.common.dto.PageIn;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
public class HelpVideoListIn extends PageIn {

	private String keyword;
	private String uploadStartTime;
	private String uploadEndTime;
	
	public void validate() {
		super.validate();
		
		if(StringUtil.isEmpty(keyword))
			keyword = null;
		
		if(StringUtil.isEmpty(uploadStartTime)) {
			uploadStartTime = null;
		} else {
			if(uploadStartTime.length() == 10)
				uploadStartTime += " 00:00:00";
		}
		
		if(StringUtil.isEmpty(uploadEndTime)) {
			uploadEndTime = null;
		} else {
			if(uploadEndTime.length() == 10)
				uploadEndTime += " 23:59:59";
		}
	}
	
}
