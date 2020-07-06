 package com.thc.platform.modules.notice.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("notice_record")
public class NoticeRecordEntity {

//	public static final int RECEIVER_TYPE_STAFF = 1;
//	public static final int RECEIVER_TYPE_PATIENT = 2;
	
	/* 状态：未读 **/
	public static final int STATUS_UNREAD = 0;
	/* 状态：已读 **/
	public static final int STATUS_READED = 1;
	/* 状态：已撤回 **/
	public static final int STATUS_RECALL = 2;
	/* 状态：已删除 **/
	public static final int STATUS_DELETE = 3;
	
	/* 可并发执行？：否 **/
	public static final int CONCURRENT_FALSE = 0;
	/* 可并发执行？：是 **/
	public static final int CONCURRENT_TRUE = 1;
	
	/* 推送MQ状态状态：失败 **/
	public static final int PUSH_MQ_STATUS_FAIL = 0;
	/* 推送MQ状态状态：成功 **/
	public static final int PUSH_MQ_STATUS_SUCCESS = 1;
	
	
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 发送应用编码
	private String appCode;
	// 发送应用业务流水号
	private String appSerialNum;
	//接收方id
	private String receiverId;
	// 通知标题，json
	private String title;
	// 通知状态 0未读 1已读
	private Integer status;
	// 模版ID
	private String templateCode;
	// 通知内容，json
	private String content;
	// 可并发执行标识
	private Integer concurrent;
	// 推送mq消息状态
	private Integer pushMqStatus;
	// 推送 撤回 mq消息状态
	private Integer pushRecallMqStatus;
	// 扩展字段
	private String extend1;
	private String extend2;
	private String extend3;
	private String extend4;
	private String extend5;
	// 备注信息
	private String notes;
	// 更新时间
	private Date updateTime;
	// 创建时间
	private Date createTime;
	
}
