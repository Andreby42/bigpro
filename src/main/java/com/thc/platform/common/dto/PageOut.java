package com.thc.platform.common.dto;

import java.util.List;

/**
 * 分页查询输出
 */
public class PageOut<T> {

	// 当前页集合
	protected List<T> items;
	// 总数量
	protected Integer totalCount;
	
	public PageOut(List<T> items, Integer totalCount) {
		this.items = items;
		this.totalCount = totalCount;
	}

	public List<T> getItems() {
		return items;
	}

	public Integer getTotalCount() {
		return totalCount;
	}
	
}
