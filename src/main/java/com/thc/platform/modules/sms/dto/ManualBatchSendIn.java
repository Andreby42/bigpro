package com.thc.platform.modules.sms.dto;

import java.io.Serializable;
import java.util.List;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ManualBatchSendIn extends SmsSendIn {

	private static final long serialVersionUID = 1L;

	/** 接收方信息 */
	// 手机号 
	protected List<ReceiverInfo> receivers;
	
	public void validate() {
		super.validate();
		if(receivers == null || receivers.isEmpty())
			throw BEUtil.illegalFormat("mobiles is empty");
	}
	
	@Data
	public static class ReceiverInfo implements Serializable {

		private static final long serialVersionUID = 1L;
		
		// 接收方类型
		private Integer type;
		// 接收人ID
		private String id;
		// 接收人名称
		private String name;
		// 手机号 
		private String mobile;
		// 发送内容
		private String content;
		
		public void validate() {
			if(StringUtil.isEmpty(mobile))
				throw BEUtil.illegalFormat("mobile is empty");
			
			if(StringUtil.isEmpty(content))
				throw BEUtil.illegalFormat("content is empty");
		}
	}
}
