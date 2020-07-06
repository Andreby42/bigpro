/**
 * Copyright 2018 人人开源 http://www.renren.io
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.thc.platform.common.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
	
	// 业务异常编码：系统错误
	public static final int FAIL_SYSTEM_ERROR = 500;
	// 业务异常编码：非法格式
	public static final int ILLEGAL_FORMAT = 1000;
	// 业务异常编码：没有权限
	public static final int AUTH_PERMISSION_DENIED = 2000;
	// 业务异常编码：业务规则受限
	public static final int FAIL_BUSI_RULE_LIMIT = 3000;
	// 业务异常编码：配置数据问题
	public static final int FAIL_CONFIG_DATA = 4000;
	// 业务异常编码：普通失败
	public static final int FAIL_NORMAL = 9000;
		
	private static final long serialVersionUID = 1l;
	
	// 错误信息
	private String msg;
	// 错误码
    private int code = FAIL_SYSTEM_ERROR;
    
    public BusinessException(String msg) {
		super(msg);
		this.msg = msg;
	}
	
	public BusinessException(String msg, Throwable e) {
		super(msg, e);
		this.msg = msg;
	}
	
	public BusinessException(String msg, int code) {
		super(msg);
		this.msg = msg;
		this.code = code;
	}
	
	public BusinessException(String msg, int code, Throwable e) {
		super(msg, e);
		this.msg = msg;
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
	
}
