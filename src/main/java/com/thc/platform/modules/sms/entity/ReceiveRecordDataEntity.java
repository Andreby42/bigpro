package com.thc.platform.modules.sms.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 短信接收记录数据实体
 */
@Data
@TableName("sms_receive_record_data")
public class ReceiveRecordDataEntity {

	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 租户ID
	private Integer tenantId;
	// 请求数据
	private String reqData;
	// 创建时间
	private Date createTime;
	
}