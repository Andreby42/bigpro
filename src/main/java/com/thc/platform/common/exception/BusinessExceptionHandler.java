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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.thc.platform.common.protocol.Api;

/**
 * 业务异常处理器
 */
@RestControllerAdvice
public class BusinessExceptionHandler {
	
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(BusinessException.class)
    public Api<Object> handleRRException(BusinessException e) {
    	return new Api<Object>(e.getCode(), e.getMessage(), null);
    }
    
    @ExceptionHandler(Exception.class)
    public Api<Object> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        if (e.getMessage() != null) {
            return new Api<Object>(Api.CODE_SYSTEM_ERROR, e.getMessage(), null);
        }
        return Api.SYSTEM_ERROR_RESULT;
    }

}
