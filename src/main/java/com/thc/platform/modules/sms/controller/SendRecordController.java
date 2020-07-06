package com.thc.platform.modules.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.sms.dto.SendRecordComplexQryIn;
import com.thc.platform.modules.sms.dto.SmsRecordDetailOut;
import com.thc.platform.modules.sms.dto.SmsRecordDetailQryIn;
import com.thc.platform.modules.sms.dto.SmsSendRecordOut;
import com.thc.platform.modules.sms.entity.ReceiveRecordEntity;
import com.thc.platform.modules.sms.entity.SendRecordEntity;
import com.thc.platform.modules.sms.service.ReceiveRecordService;
import com.thc.platform.modules.sms.service.SendRecordService;

/**
 * 短信发送记录接口类
 */
@RestController
@RequestMapping("/sms/send-record")
public class SendRecordController {

	@Autowired
	private SendRecordService sendRecordService;
	@Autowired
	private ReceiveRecordService receiveRecordService;
	
	/**
	 * 综合查询
	 */
	@PostMapping("/complexQry")
	public Api<List<SendRecordEntity>> complexQry(@RequestBody SendRecordComplexQryIn in) {
		in.validate();
		
		PageOut<SendRecordEntity> pageOut = sendRecordService.complexQry(in);
		
		return Api.ok(pageOut.getItems(), pageOut.getTotalCount());
	}
	
	/**
	 * 按业务ID查询（租户ID、应用编码、应用流水号）
	 * @param tenantId 租户ID
	 * @param appCode 应用编码
	 * @param appSerialNum 应用流水号
	 * @return 短信发送记录
	 */
	@GetMapping("/qryByBusiId")
	public Api<SmsSendRecordOut> qryByBusiId(
			@RequestParam("tenantId") Integer tenantId
			, @RequestParam("appCode") String appCode
			, @RequestParam("appSerialNum") String appSerialNum) {
		
		ReceiveRecordEntity receiveRecord = receiveRecordService.getByBusiId(tenantId, appCode, appSerialNum);
		SendRecordEntity sendRecord = sendRecordService.getByReceiveRecordId(receiveRecord.getId());
		
		return Api.ok(new SmsSendRecordOut(receiveRecord, sendRecord));
	}
	
	/**
	 * 短信发送记录详情查询接口
	 */
	@PostMapping("/detailComplexQry")
	public Api<List<SmsRecordDetailOut>> detailComplexQry(@RequestBody SmsRecordDetailQryIn in) {
		in.validate();
		
		PageOut<SmsRecordDetailOut> pageOut = sendRecordService.detailComplexQry(in);
		
		return Api.ok(pageOut.getItems(), pageOut.getTotalCount());
	}
	
}
