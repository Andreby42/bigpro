package com.thc.platform.common.util;

import com.thc.platform.common.exception.BusinessException;

/**
 * 业务异常（BusinessException） 工具类
 */
public class BEUtil {

	/** 入参格式校验失败 */
	public static BusinessException illegalFormat(String msg) {
		return new BusinessException(msg, BusinessException.ILLEGAL_FORMAT);
	}
	
	/** 认证失败 */
	public static BusinessException authPermissionDenied(String msg) {
		return new BusinessException(msg, BusinessException.AUTH_PERMISSION_DENIED);
	}
	
	/** 业务规则校验失败 */
	public static BusinessException failBusiRuleLimit(String msg) {
		return new BusinessException(msg, BusinessException.FAIL_BUSI_RULE_LIMIT);
	}
	
	/** 配置数据不正确 */
	public static BusinessException failCauseConfigData(String msg) {
		return new BusinessException(msg, BusinessException.FAIL_CONFIG_DATA);
	}
	
	/** 普通业务异常 */
	public static BusinessException failNormal(String msg) {
		return new BusinessException(msg, BusinessException.FAIL_NORMAL);
	}
	
	/** 系统异常 */
	public static BusinessException systemError() {
		return new BusinessException("系统错误，请联系管理员。", BusinessException.FAIL_SYSTEM_ERROR);
	}
	
}
