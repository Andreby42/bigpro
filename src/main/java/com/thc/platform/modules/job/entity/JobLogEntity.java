package com.thc.platform.modules.job.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("job_log")
public class JobLogEntity {

	public static final int STATUS_FAIL = 0;
	public static final int STATUS_SUCCESS = 1;
	public static final int STATUS_LOCK_FAIL = 2;
	
	// id
	@TableId(type = IdType.INPUT)
	private String id;
	// 任务名称
	private String jobName;
	// 起始时间
	private Date startTime;
	// 结束时间
	private Date endTime;
	// 耗费毫秒数
	private Long consumeMillis;
	// 状态
	private Integer status;
	// 备注
	private String notes;
	// 创建时间
	private Date createTime;
}
