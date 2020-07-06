package com.thc.platform.modules.ocr.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author @date             @version
 * zouyu            2020-02-01         1.0.0
 * *******************************************************
 * @Description: OcrInspectionResultItem
 * <p>
 * *******************************************************
 */
@Setter
@Getter
public class OcrInspectionResultItem {

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
     * 识别结果ID
     */
    private String ocrInspectionResultId;

    /**
     * 序号
     */
    private String no;

    /**
     * 检验项目代码
     */
    private String code;

    /**
     * 检验项目名称
     */
    private String name;

    /**
     * 检验结果
     */
    private String result;

    /**
     * 单位
     */
    private String unit;

    /**
     * 正常值范围
     */
    private String ref;

    /**
     * 状态 0-正常 1-高于正常值 2-低于正常值
     */
    private Integer status;

    /**
     * 创建人ID
     */
    private String creator;

    /**
     * 更新人ID
     */
    private String updater;

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
}