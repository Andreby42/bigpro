package com.thc.platform.modules.apppush.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@TableName("app_push_record")
public class AppPushRecordEntity {

	public static final int STATUS_FAIL = 0;
	public static final int STATUS_SUCCESS = 1;
	
	private String id;
	private String reqSerialNum;
	private String reqData;
	private String extPlatfromResData;
	private Integer status;
	private String notes;
	private Date createTime;

}
