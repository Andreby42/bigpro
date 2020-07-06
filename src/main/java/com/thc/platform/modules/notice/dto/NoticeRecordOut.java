package com.thc.platform.modules.notice.dto;

import com.thc.platform.modules.notice.entity.NoticeRecordEntity;

public class NoticeRecordOut {

	private NoticeRecordEntity entity;

	public NoticeRecordOut(NoticeRecordEntity entity) {
		this.entity = entity;
	}

	public String getId() {
		return entity.getId();
	}
	
	public String getReceiverId() {
		return entity.getReceiverId();
	}

	public String getTitle() {
		return entity.getTitle();
	}

	public Integer getStatus() {
		return entity.getStatus();
	}

	public String getContent() {
		return entity.getContent();
	}

	public String getExtend1() {
		return entity.getExtend1();
	}

	public String getExtend2() {
		return entity.getExtend2();
	}

	public String getExtend3() {
		return entity.getExtend3();
	}

	public String getExtend4() {
		return entity.getExtend4();
	}

	public String getExtend5() {
		return entity.getExtend5();
	}

}
