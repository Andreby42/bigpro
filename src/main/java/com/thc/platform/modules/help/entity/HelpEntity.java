package com.thc.platform.modules.help.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 帮助
 */
@Data
@TableName("help")
public class HelpEntity {

	public static final String ID_THC = "thc";
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 名称
	private String name;
	
}
