package com.thc.platform.modules.ocr.util.constant;

import com.google.common.collect.ImmutableList;

import java.util.List;

public interface OcrConstants {

    /**
     * 图片上传渠道
     */
    interface SourceType {
        Integer THC = 1;                    // thc系统
        Integer PHONE = 2;                  // 移动端
    }

    /**
     * 图片类型
     */
    interface ImgType {
        Integer INSPECTION = 1;             // 检验单
        Integer EXAMINE = 2;                // 检查单
    }

    /**
     * 识别结果类型
     */
    interface Result {
        Integer SUCCESS = 1;                // 成功
        Integer FAIL = 2;                   // 失败
    }

    /**
     * 操作人员类型
     */
    interface OperationType {
        Integer DOCTOR = 1;                 // 医生
        Integer PATIENT = 2;                // 患者
    }

    /**
     * 识别结果类型
     */
    interface ResultType {
        Integer BAI_DU = 1;                 // 百度AI识别结果
        Integer MANUAL = 2;                 // 人工手动录入
    }

    interface InspectionResultStatus {
        Integer HIGH = 1;
        Integer LOW = 2;
    }

    List<String> INSPECTION_DATE_TYPE_FIELD = ImmutableList.of(
            "applicationDate",
            "collectionDate",
            "reportDate",
            "receiveDate"
    );
    List<String> EXAMINE_DATE_TYPE_FIELD = ImmutableList.of(
            "reportDate",
            "examineDate"
    );

}
