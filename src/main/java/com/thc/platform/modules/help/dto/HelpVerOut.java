package com.thc.platform.modules.help.dto;

public class HelpVerOut {

	// ID
	private String id;
	// 版本
	private String ver;

	public HelpVerOut(String id, String ver) {
		this.id = id;
		this.ver = ver;
	}

	public String getId() {
		return id;
	}

	public String getVer() {
		return ver;
	}

}
