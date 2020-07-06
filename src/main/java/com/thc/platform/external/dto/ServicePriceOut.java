package com.thc.platform.external.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicePriceOut {


    /**
     * 集团id;
     */

    private String tenantId;

    /**
     * 服务类型，0短信，1查快递
     */
    private Integer type;

    /**
     * 当前价格;
     */
    private BigDecimal price;
}
