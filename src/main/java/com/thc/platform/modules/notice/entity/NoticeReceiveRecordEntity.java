 package com.thc.platform.modules.notice.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("notice_receive_record")
public class NoticeReceiveRecordEntity {

	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 发送应用编码
	private String appCode;
	// 发送应用业务流水号
	private String appSerialNum;
	// 请求数据
	private String reqData;
	// 备注信息
	private String notes;
	// 创建时间
	private Date createTime;
	
}
