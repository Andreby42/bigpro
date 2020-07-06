package com.thc.platform.external.dto;

import java.util.List;

import lombok.Data;

@Data
public class ListTenantByTypesIn {
	
	// 租户ID
	private List<Integer> tenantTypes;
	// 租户名称
	private String tenantName;
	// 分页起始位置
	private Integer offset;
	// 分页大小
	private Integer pagesize;
	
}
