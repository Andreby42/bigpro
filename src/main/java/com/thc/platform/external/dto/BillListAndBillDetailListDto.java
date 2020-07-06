package com.thc.platform.external.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BillListAndBillDetailListDto implements Serializable {
    /** 费用类型：短信扣费*/
    public static final int FEE_TYPE_DEDUCTION_SMS = 2;
    /** 费用类型：快递查询扣费*/
    public static final int FEE_TYPE_DEDUCTION_EXPRESS = 3;
    /**
     * 费用类型：百度OCR识别扣费
     */
    public static final int FEE_TYPE_DEDUCTION_OCR = 4;

    /** 服务类型 ：短信*/
    public static final int SERVICE_TYPE_SMS = 0;
    /** 服务类型：快递*/
    public static final int SERVICE_TYPE_EXPRESS = 1;
    /**
     * 服务类型：百度OCR识别
     */
    public static final int SERVICE_TYPE_OCR = 2;

    @Data
    public static class TenantAccountBill implements Serializable {
        /**
         * 订单号;
         */
        private String orderId;

        /**
         * 集团id;
         */
        private String tenantId;

        /**
         * 费用类型：2短信扣费，3快递查询扣费
         */
        private Integer feeType;

        /**
         * 金额;
         */
        private BigDecimal money;
    }
    @Data
    public static class TenantAccountBillDetail implements Serializable {
        /**
         * 订单号;
         */
        private String orderId;

        /**
         * 集团id;
         */
        private String tenantId;

        /**
         * 机构id;
         */
        private String orgId;

        /**
         * 机构名称;
         */
        private String orgName;

        /**
         * 服务类型 0短信，1快递
         */
        private Integer serviceType;

        /**
         * 账单时间;
         */
        private String billDate;

        /**
         * 消费数量;
         */
        private Integer serviceCount;

        /**
         * 金额;
         */
        private BigDecimal consumeMoney;
    }
}
