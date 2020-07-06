package com.thc.platform.modules.sms.dto;

import java.io.Serializable;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class SysTemplateSingleSendIn extends SmsSendIn {

	private static final long serialVersionUID = 1L;

	/** 接收方信息 */
	private ReceiverInfo receiver;
	
	
	/** 短信模版信息 */
	// 短信模版ID
	private String templateId;
	// 传入参数
	private String payload;
	// 短信发送节点编码
	private String smsSendNodeCode;
	
	public void validate() {
		super.validate();
		
		if(StringUtil.isEmpty(receiver.getMobile()))
			throw BEUtil.illegalFormat("mobile is empty");
		
		if(StringUtil.isEmpty(templateId))
			throw BEUtil.illegalFormat("templateId is empty");
		
		if(StringUtil.isEmpty(payload))
			throw BEUtil.illegalFormat("payload is empty");
		
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
	}
	
}
