package com.thc.platform.modules.apppush.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("app_push_app")
public class AppEntity {

	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 名称
	private String name;
	// 备注
	private String notes;
	
}
