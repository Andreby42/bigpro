package com.thc.platform.modules.notice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("notice_template")
public class NoticeTemplateEntity extends ModifyBaseEntity {
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 编码
	private String code;
	// 名称
	private String name;
	// 内容 带参数
	private String content;
	
}
