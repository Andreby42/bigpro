package com.thc.platform.modules.apppush.dto.android;

import java.text.SimpleDateFormat;

import com.thc.platform.common.util.BEUtil;

import lombok.Data;

@Data
public class DurationIn {

	private String begin;
	private String end;
	
	public void validate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long beginMillis = 0l;
		long endMillis = 0l;
		try {
			beginMillis = sdf.parse(begin).getTime();
			endMillis = sdf.parse(end).getTime();
		} catch (Exception e) {
			throw BEUtil.illegalFormat("DateFormat: yyyy-MM-dd HH:mm:ss");
		}
		if ((beginMillis <= 0L) || (endMillis <= 0L)) 
			throw BEUtil.illegalFormat("DateFormat: yyyy-MM-dd HH:mm:ss");
		
		if (beginMillis > endMillis) 
			throw BEUtil.illegalFormat("startTime should be smaller than endTime");
	}
	
}
