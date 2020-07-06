package com.thc.platform.modules.help.dto;

import java.util.List;

import lombok.Data;

@Data
public class HelpMenuTreeOut {

	private String helpId;
	private String ver;
	
	private List<MenuNodeOut> menus;
	
	
	@Data
	public static class MenuNodeOut {
		
		private String rootId;
		private String id;
		private String name;
		
		private List<MenuNodeOut> nodes;
		
	}
	
}
