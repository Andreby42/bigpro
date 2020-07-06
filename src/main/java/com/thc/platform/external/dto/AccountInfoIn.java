package com.thc.platform.external.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountInfoIn {
	/**
	 * 集团id;
	 */
	private String tenantId;

	/**
	 * 集团名称;
	 */
	private String tenantName;


}
