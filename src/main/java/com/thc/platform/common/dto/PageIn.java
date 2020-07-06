package com.thc.platform.common.dto;

/**
 * 分页查询输入
 */
public class PageIn {

	// 分页起始位置
	protected Integer offset;
	// 分页大小
	protected Integer pagesize;

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public Integer getPagesize() {
		return pagesize;
	}

	public void setPagesize(Integer pagesize) {
		this.pagesize = pagesize;
	}

	public void validate() {
		if (offset == null || offset < 0)
			offset = 0;

		if (pagesize == null || pagesize <= 0 || pagesize > 100)
			pagesize = 100;
	}
}
