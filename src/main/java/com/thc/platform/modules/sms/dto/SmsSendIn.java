package com.thc.platform.modules.sms.dto;

import java.io.Serializable;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.sms.entity.TemplateSignTypeEntity;

import lombok.Data;

@Data
public class SmsSendIn implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 基本信息 */
	// 租户ID
	private Integer tenantId;
	// 发送应用编码
	private String appCode;
	// 发送应用业务流水号
	private String appSerialNum;
	// 短信签名ID
	private String signTypeId;
	
	/** 机构科室信息 */
	private OrgSubjectInfo orgSubject;
	/** 发送方信息 */
	protected SenderInfo sender;
	
	public void validate() {
		
		if(StringUtil.isEmpty(appCode))
			throw BEUtil.illegalFormat("appCode is empty");
		
		if(StringUtil.isEmpty(appSerialNum))
			throw BEUtil.illegalFormat("appSerialNum is empty");
		
		if (tenantId == null)
			throw BEUtil.illegalFormat("tenantId is null");
		
		if(StringUtil.isEmpty(signTypeId))
			signTypeId = TemplateSignTypeEntity.ID_DEFAULT;
	}
	
	// 机构科室信息
	@Data
	public static class OrgSubjectInfo implements Serializable {

		private static final long serialVersionUID = 1L;
		
		// 机构ID
		private String orgId;
		// 机构名称
		private String orgName;
		// 科室ID
		private String subjectId;
		// 科室名称
		private String subjectName;
		
	}
	
	// 发送方信息
	@Data
	public static class SenderInfo implements Serializable {

		private static final long serialVersionUID = 1L;
		
		// 发送方类型
		private Integer type;
		// 发送人ID
		private String id;
		// 发送人名称
		private String name;
		
	}
	
}
