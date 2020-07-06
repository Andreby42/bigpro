package com.thc.platform.modules.help.dto;

import java.util.Date;

import lombok.Data;

@Data
public class HelpMenuSearchOut {

	private String menuId;
	private String menuName;
	private Date modifyTime;
	
	public HelpMenuSearchOut(String menuId, String menuName, Date modifyTime) {
		this.menuId = menuId;
		this.menuName = menuName;
		this.modifyTime = modifyTime;
	}
	
}
