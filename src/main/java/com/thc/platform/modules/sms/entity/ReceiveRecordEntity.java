package com.thc.platform.modules.sms.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 接收记录实体
 */
@Data
@TableName("sms_receive_record")
public class ReceiveRecordEntity {

	/** 状态：已接收 */
	public static final int STATUS_RECEIVED = 0;
	/** 状态：成功 */
	public static final int STATUS_SUCCESS = 1;
	/** 状态：失败 */
	public static final int STATUS_FAIL = 2;
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 租户ID
	private Integer tenantId;
	/** 发送应用信息 */
	// 发送应用编码
	private String appCode;
	// 发送业务流水号
	private String appSerialNum;
	// 状态
	private Integer status;
	// 备注
	private String notes;
	//
	private Date createTime;
	
}