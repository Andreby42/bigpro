package com.thc.platform.modules.sms.dto;

import java.io.Serializable;
import java.util.List;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ManualCcSendIn extends SmsSendIn {

	private static final long serialVersionUID = 1L;
	
	/** 接收方信息 */
	private ReceiverInfo receiver;
	
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
		protected List<String> mobiles;
		// 发送内容
		private String content;
		
		public void validate() {
			if(mobiles != null && mobiles.isEmpty())
				throw BEUtil.illegalFormat("mobile is empty");
			
			if(StringUtil.isEmpty(content))
				throw BEUtil.illegalFormat("content is empty");
		}
	}
	
}
