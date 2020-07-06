package com.thc.platform.modules.sms.dto;

import java.util.Date;

import lombok.Data;

@Data
public class SmsRecordDetailOut {

	// 租户ID
	private Integer tenantId;
	// 发送应用编码
	private String appCode;
	// 发送业务流水号
	private String appSerialNum;
	// 手机号 
	private String mobiles;

	private ReceiveRecordOut receiveRecord;
	private SendRecordOut sendRecord;
	
	@Data
	public static class ReceiveRecordOut {
		// 状态
		private Integer status;
		// 请求数据
		private String reqData;
		// 备注
		private String notes;
		//
		private Date createTime;
	}
	
	@Data
	public static class SendRecordOut {
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
		// 外部平台响应数据
		private String externalResData;
		// 外部短信平台返回码
		private String externalResCode;
		
		// 备注信息
		private String notes;
		// 创建时间
		private Date createTime;
	}
	
}
