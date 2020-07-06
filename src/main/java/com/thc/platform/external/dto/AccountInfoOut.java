package com.thc.platform.external.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountInfoOut {
	/**
	 * 集团id;
	 */
	private String tenantId;

	/**
	 * 集团名称;
	 */
	private String tenantName;

	/**
	 * 当前余额;
	 */
	private BigDecimal balance;

	/**
	 * 冻结金额;
	 */
	private BigDecimal freezeFee;

}
