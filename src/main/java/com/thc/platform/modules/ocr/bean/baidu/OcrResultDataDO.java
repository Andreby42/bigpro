package com.thc.platform.modules.ocr.bean.baidu;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 百度识别出来未转换的结构化数据
 */

@Setter
@Getter
public class OcrResultDataDO {

    private String templateSign;
    private String templateName;
    private String scores;
    private List<OcrResultDataKVDo> ret;

    @Setter
    @Getter
    public static class OcrResultDataKVDo {
        private String word_name;
        private String word;
    }
}
