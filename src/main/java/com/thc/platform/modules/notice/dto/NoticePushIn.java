package com.thc.platform.modules.notice.dto;

import java.io.Serializable;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.notice.entity.NoticeRecordEntity;

import lombok.Data;

@Data
public class NoticePushIn implements Serializable {

	private static final long serialVersionUID = 1L;

	// 发送应用编码
	private String appCode;
	// 发送应用业务流水号
	private String appSerialNum;
	//接收方id
	private String receiverIds;
	// 模版ID
	private String templateCode;
	// 传入参数
	private String payload;
	// 可并发执行标识
	private Integer concurrent;
	// 扩展字段
	private String extend1;
	private String extend2;
	private String extend3;
	private String extend4;
	private String extend5;
	
	public void validate() {
		
		if(StringUtil.isEmpty(appCode))
			throw BEUtil.illegalFormat("appCode is empty");
		
		if(StringUtil.isEmpty(appSerialNum))
			throw BEUtil.illegalFormat("appSerialNum is empty");
		
		if(StringUtil.isEmpty(receiverIds))
			throw BEUtil.illegalFormat("receiverIds is empty");
		
		if(StringUtil.isEmpty(templateCode))
			throw BEUtil.illegalFormat("templateCode is empty");
		
		if(concurrent == null)
			concurrent = NoticeRecordEntity.CONCURRENT_FALSE;
		
	}
}
