package com.thc.platform.modules.sms.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

/**
 * 短信发送记录实体
 */
@Data
@TableName("sms_send_record")
public class SendRecordEntity {

	/** 模版类型：验证码 */
	public static final int TYPE_VERIFY = 0;
	/** 模版类型：通知 */
	public static final int TYPE_NOTICE = 1;
	
	/** 状态：失败 */
	public static final int STATUS_FAIL = 0;
	/** 状态：成功 */
	public static final int STATUS_SUCCESS = 1;
	
	/** 基本信息 */
	// ID
	@TableId(type = IdType.INPUT)
	private String id;
	// 租户ID
	private Integer tenantId;
	// 接收记录ID
	private  String receiveRecordId;
	
	/** 短信模版信息 */
	// 短信模版ID
	private String templateId;
	// 传入参数
	private String payload;
	
	/** 机构科室信息 */
	// 机构ID
	private String orgId;
	// 机构名称
	private String orgName;
	// 科室ID
	private String subjectId;
	// 科室名称
	private String subjectName;
	
	/** 发送方信息 */
	// 发送方类型
	protected Integer senderType;
	// 发送人ID
	private String senderId;
	// 发送人名称
	private String senderName;
	
	/** 接收方信息 */
	// 接收方类型
	protected Integer receiverType;
	// 接收人ID
	private String receiverId;
	// 接收人名称
	private String receiverName;
	// 手机号 
	private String mobiles;
	
	// 短信类型
	private Integer type;
	// 短信签名ID
	private String signTypeId;
	// 内容
	private String content;
	// 状态
	private Integer status;
	// 短信发送计费数量。短信过长时，不同平台会根据其具体规则进行拆分
	private Integer feeNum;
		
	/** 外部平台调用信息 */
	// 外部平台模版ID
	private String externalTemplateId;
	// 外部平台请求数据
//	private String externalReqData;
	// 外部平台响应数据
	private String externalResData;
	// 外部短信平台返回码
	private String externalResCode;
	
	// 备注信息
	private String notes;
	// 创建时间
	private Date createTime;
	
}
