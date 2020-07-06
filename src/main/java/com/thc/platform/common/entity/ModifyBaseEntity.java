package com.thc.platform.common.entity;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
public abstract class ModifyBaseEntity extends CreateBaseEntity {

	protected String modifyUserId;
	protected String modifyUserName;
	protected Date modifyTime;

}
