package com.thc.platform.external.dto;

import lombok.Data;

@Data
public class ServicePriceIn {
    /**
     * 服务类型 ：短信
     */
    public static final int TYPE_SMS = 0;
    /**
     * 服务类型：快递
     */
    public static final int TYPE_EXPRESS = 1;
    /**
     * 服务类型：OCR识别
     */
    public static final int TYPE_OCR = 2;

    /**
     * 集团id;
     */
    private String tenantId;

    /**
     * 服务类型，0短信，1查快递
     */
    private Integer type;
}
