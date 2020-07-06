package com.thc.platform.modules.sms.dto;

import java.util.Date;

import com.thc.platform.common.dto.PageIn;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SendRecordComplexQryIn extends PageIn {

	// 租户ID
	private Integer tenantId;
	// 手机号 
	private String mobile;
	// 短信类型
	private Integer type;
	// 状态
	private Integer status;
	// 短信签名ID
	private String signTypeId;
	// 短信模版ID
	private String templateId;
		
	/** 机构科室信息 */
	private OrgSubject orgSubject;
	/** 发送方信息 */
	private Sender sender;
	/** 接收方信息 */
	private Receiver receiver;
	/** 时间范围 */
	private Date startTime;
	private Date endTime;
	
	public void validate() {
		orgSubject.validate();
		sender.validate();
		receiver.validate();
	}
	
	/** 机构科室信息 */
	@Data
	public class OrgSubject {
		// 机构ID
		private String orgId;
		// 机构名称
		private String orgName;
		// 科室ID
		private String subjectId;
		// 科室名称
		private String subjectName;
		
		public void validate() {
			if(StringUtil.isNotEmpty(orgId))
				orgName = null;
			
			if(StringUtil.isNotEmpty(subjectId))
				subjectName = null;
		}
		
	}
	
	/** 发送方信息 */
	@Data
	public static class Sender {
		// 发送方类型
		protected Integer senderType;
		// 发送人ID
		private String senderId;
		// 发送人名称
		private String senderName;
		
		public void validate() {
			if(StringUtil.isNotEmpty(senderId))
				senderName = null;
		}
	}
	
	/** 接收方信息 */
	@Data
	public static class Receiver {
		// 接收方类型
		protected Integer receiverType;
		// 接收人ID
		private String receiverId;
		// 接收人名称
		private String receiverName;
		
		public void validate() {
			if(StringUtil.isNotEmpty(receiverId))
				receiverName = null;
		}
	}
	
}
