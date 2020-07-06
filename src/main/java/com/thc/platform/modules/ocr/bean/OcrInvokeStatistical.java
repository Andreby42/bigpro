package com.thc.platform.modules.ocr.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author @date             @version
 * zouyu            2020-02-01         1.0.0
 * *******************************************************
 * @Description: OcrInvokeStatistical
 * <p>
 * *******************************************************
 */
@Setter
@Getter
@ToString
public class OcrInvokeStatistical {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 机构ID
     */
    private String orgId;

    /**
     * 统计时间
     */
    private Date statisticalDate;

    /**
     * 成功调用次数
     */
    private Integer number;

    private Integer type;

    private BigDecimal fee;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    private Integer offset;

    private Integer pagesize;

    /********************* 非实体类字段 *****************************/
    private Date statisticalStartDate;
    private Date statisticalEndDate;
    private String tenantName;
    private String orgName;
}