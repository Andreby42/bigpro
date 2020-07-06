package com.thc.platform.modules.help.dto;

import com.thc.platform.common.dto.PageIn;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
public class HelpFeedbackListIn extends PageIn {

	private String startTime;
	private String endTime;
	

	public void validate() {
		super.validate();
		
		if(StringUtil.isEmpty(startTime)) {
			startTime = null;
		} else {
			if(startTime.length() == 10)
				startTime += " 00:00:00";
		}
		
		if(StringUtil.isEmpty(endTime)) {
			endTime = null;
		} else {
			if(endTime.length() == 10)
				endTime += " 23:59:59";
		}
	}
}
