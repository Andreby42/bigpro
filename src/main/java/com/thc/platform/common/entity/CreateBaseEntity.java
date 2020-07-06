package com.thc.platform.common.entity;

import java.util.Date;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class CreateBaseEntity {

	protected String creatorId;
	protected String creatorName;
	protected Date createTime;

}
