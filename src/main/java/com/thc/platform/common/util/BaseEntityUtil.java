package com.thc.platform.common.util;

import java.util.Date;

import com.thc.platform.common.entity.CreateBaseEntity;
import com.thc.platform.common.entity.ModifyBaseEntity;

/**
 * 基础实体工具类
 */
public class BaseEntityUtil {

	/**
	 * 设置实体创建人相关信息（ID、名称、创建时间）
	 * @param entity
	 * @param userId 用户ID
	 * @param userName 用户名称
	 * @param currentTime 当前时间
	 */
	public static void setCreateBaseEntityInfo(CreateBaseEntity entity, String userId, String userName, Date currentTime) {
		entity.setCreatorId(userId);
		entity.setCreatorName(userName);
		entity.setCreateTime(currentTime);
	}
	
	/**
	 * 设置实体创建人相关信息（ID、名称创建时间）
	 * 创建时间取当前系统时间
	 * @param entity 实体对象
	 * @param userId 用户ID
	 * @param userName 用户名称
	 */
	public static void setCreateBaseEntityInfo(CreateBaseEntity entity, String userId, String userName) {
		setCreateBaseEntityInfo(entity, userId, userName, new Date()); 
	}
	
	/**
	 * 设置实体修改人相关信息（ID、名称、修改时间）
	 * @param entity
	 * @param userId 用户ID
	 * @param userName 用户名称
	 * @param currentTime 当前时间
	 */
	public static void setModifyBaseEntityInfo(ModifyBaseEntity entity, String userId, String userName, Date currentTime) {
		entity.setModifyUserId(userId);
		entity.setModifyUserName(userName);
		entity.setModifyTime(currentTime);
	}
	
	/**
	 * 设置实体修改人相关信息（ID、名称创建时间）
	 * 修改时间取当前系统时间
	 * @param entity 实体对象
	 * @param userId 用户ID
	 * @param userName 用户名称
	 */
	public static void setModifyBaseEntityInfo(ModifyBaseEntity entity, String userId, String userName) {
		setModifyBaseEntityInfo(entity, userId, userName, new Date()); 
	}
	
	/**
	 * 设置实体创建人（ID、名称、创建时间）与 修改人相关信息（ID、名称、修改时间）
	 * @param entity
	 * @param userId 用户ID
	 * @param userName 用户名称
	 * @param currentTime 当前时间
	 */
	public static void setCreateAndModifyBaseEntityInfo(ModifyBaseEntity entity, String userId, String userName, Date currentTime) {
		setCreateBaseEntityInfo(entity, userId, userName, currentTime); 
		setModifyBaseEntityInfo(entity, userId, userName, currentTime);
	}
	
	/**
	 * 设置实体创建人（ID、名称、创建时间）与 修改人相关信息（ID、名称、修改时间）
	 * 创建时间、修改时间取当前系统时间
	 * @param entity
	 * @param userId 用户ID
	 * @param userName 用户名称
	 */
	public static void setCreateAndModifyBaseEntityInfo(ModifyBaseEntity entity, String userId, String userName) {
		setCreateAndModifyBaseEntityInfo(entity, userId, userName, new Date()); 
	}
	
}
