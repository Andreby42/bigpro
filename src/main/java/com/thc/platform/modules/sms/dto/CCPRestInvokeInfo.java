package com.thc.platform.modules.sms.dto;

import lombok.Data;

@Data
public class CCPRestInvokeInfo {

	private boolean invokeSuccess = false;
	private boolean busiSuccess = false;
	
	private Integer httpResCode;
	private String reqData;
	private String resData;
	private String resCode;
	private String notes;
	private String templateId;
	private int feeNum = 0;
	
	public CCPRestInvokeInfo fail(String msg) {
		invokeSuccess = false;
		notes = msg;
		
		return this;
	}
	
	public CCPRestInvokeInfo success(String resData) {
		invokeSuccess = true;
		this.resData = resData;
		
		return this;
	}
	
	public CCPRestInvokeInfo busiFail(String resCode, String msg) {
		busiSuccess = false;
		this.resCode = resCode;
		notes = msg;
		
		return this;
	}
	
	public CCPRestInvokeInfo busiSuccess(String resCode) {
		busiSuccess = true;
		this.resCode = resCode;
		
		return this;
	}
	
	public boolean isSuccess() {
		return invokeSuccess && busiSuccess;
	}
}
