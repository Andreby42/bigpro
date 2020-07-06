package com.thc.platform.common.protocol;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thc.platform.common.exception.BusinessException;

import lombok.Data;

/**
 * RestApi 返回协议类
 */
@JsonInclude(Include.NON_NULL)
@Data
public class Api<T> {

	/** 返回码：成功 */
	public static final int CODE_SUCCESS = 0;
	/** 返回码：系统错误 */
	public static final int CODE_SYSTEM_ERROR = 500;
	/** 返回码：未授权 */
	public static final int CODE_UNANTHORIZED = 401;
	/** 返回码：禁止 */
	public static final int CODE_FORBIDDEN = 403;
	/** 返回码：普通错误 */
	public static final int CODE_FAIL_NORMAL = BusinessException.FAIL_NORMAL;

	/** 静态RestApi返回：成功 */
	public static final Api<Object> SUCCESS_RESULT = Api.ok(null);
	/** 静态RestApi返回：系统错误 */
	public static final Api<Object> SYSTEM_ERROR_RESULT = Api.systemError(null);
	/** 静态RestApi返回：未授权 */
	public static final Api<Object> UNANTHORIZED_RESULT = Api.error(CODE_UNANTHORIZED, "未授权用户");
//	public static final ApiProtocol<Object> FORBIDDEN_RESULT = ApiProtocol.error(CODE_FORBIDDEN, "禁止访问");
	/** 静态RestApi返回：token过期 */
	public static final Api<Object> TOKEN_EXP_RESULT = Api.error(CODE_UNANTHORIZED, "登录过期");
	
	private int code;
	private String msg;
	private Head head;
	private T data;
	private Long totalCount;

	public Api() {}
	
	public Api(int code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		head = new Head(code, msg);
		this.data = data;
	}
	
	private Api(T data, Long totalCount) {
		this.code = CODE_SUCCESS;
		head = new Head(code, msg);
		this.data = data;
		this.totalCount = totalCount;
	}
	
	@Override
	public String toString() {
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "";
		}
	}

	@JsonInclude(Include.NON_NULL)
	public static class Head {

		private Integer errCode;
		private String errMsg;

		public Head() {
		}

		public Head(Integer errCode, String errMsg) {
			this.errCode = errCode;
			this.errMsg = errMsg;
		}
		
		public Integer getErrCode() {
			return errCode;
		}

		public String getErrMsg() {
			return errMsg;
		}

	}
	
	/** 快速构建返回值方法：成功 */
	public static <T> Api<T> ok(T data) {
		return new Api<T>(CODE_SUCCESS, null, data);
	}

	/**
	 * 快速构建返回值方法：成功
	 * 用于分页返回时，设置记录总数
	 */
	public static <T> Api<T> ok(T data, long totalCount) {
		return new Api<T>(data, totalCount);
	}
	
	/** 快速构建返回值方法：失败 */
	public static <T> Api<T> error(String msg) {
		return new Api<T>(CODE_FAIL_NORMAL, msg, null);
	}
	
	/** 快速构建返回值方法：失败（可指定错误码） */
	public static <T> Api<T> error(int code, String msg) {
		return new Api<T>(code, msg, null);
	}
	/** 快速构建返回值方法：失败（系统错误） */
	public static <T> Api<T> systemError(String msg) {
		return new Api<T>(CODE_SYSTEM_ERROR, msg, null);
	}
	
	@JsonIgnore
	public boolean isSuccess() {
		return CODE_SUCCESS == code;
	}
	
}