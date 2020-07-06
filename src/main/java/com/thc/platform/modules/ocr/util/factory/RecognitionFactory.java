package com.thc.platform.modules.ocr.util.factory;


import com.thc.platform.modules.ocr.util.SpringUtil;

/**
 * 识别工具简单工厂
 */
public class RecognitionFactory {

    /**
     * 当前默认返回百度文字识别工具
     *
     * @return -
     */
    public static AbstractRecognition createRecognition() {
        return SpringUtil.getBean(BaiduRecognition.class);
    }

}
