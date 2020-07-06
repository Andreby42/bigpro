package com.thc.platform.modules.sms.dto;

import com.thc.platform.modules.sms.entity.ReceiveRecordEntity;
import com.thc.platform.modules.sms.entity.SendRecordEntity;

import lombok.Data;

@Data
public class SmsSendRecordOut {

	private ReceiveRecordEntity receiveRecord;
	private SendRecordEntity sendRecord;

	public SmsSendRecordOut(ReceiveRecordEntity receiveRecord, SendRecordEntity sendRecord) {
		this.receiveRecord = receiveRecord;
		this.sendRecord = sendRecord;
	}

}
