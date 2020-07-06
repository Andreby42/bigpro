package com.thc.platform.modules.apppush.dto.ios;

import java.math.BigDecimal;

import com.thc.platform.common.util.BEUtil;

import lombok.Data;

@Data
public class DictionarySoundIn {

	// 临界警报标志。设置为1以启用临界警报
	private Integer critical;
	// 声音文件名
	private String name;
	// 临界警报声音的音量。将此设置为0（无声）和1（全音量）之间的值
	private Double volume;

	public void validate() {
		if ((volume > 1.0D) || (volume < 0.0D)) 
			throw BEUtil.illegalFormat("volume of sound_d should between 0.0 and 1.0");
		
		volume = new BigDecimal(volume).setScale(2, 4).doubleValue();
	}
	
}
