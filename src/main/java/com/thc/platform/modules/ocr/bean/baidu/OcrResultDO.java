package com.thc.platform.modules.ocr.bean.baidu;

import lombok.Getter;
import lombok.Setter;

/**
 * 百度识别出来未转换的结构化数据
 */
@Setter
@Getter
public class OcrResultDO {

    private String log_id;
    private String error_msg;
    private String error_code;
    private OcrResultDataDO data;

}
