package com.thc.platform.modules.notice.dto;

import java.util.Date;

import com.thc.platform.common.dto.PageIn;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NoticeSearchIn extends PageIn {

	private Date startTime;
	private Date endTime;
	
	public void validate() {
		super.validate();
	}
	
}
