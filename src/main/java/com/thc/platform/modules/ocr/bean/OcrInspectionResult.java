package com.thc.platform.modules.ocr.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * @author @date             @version
 * zouyu            2020-02-01         1.0.0
 * *******************************************************
 * @Description: OcrInspectionResult
 * <p>
 * *******************************************************
 */
@Setter
@Getter
public class OcrInspectionResult {

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
     * ocr识别记录ID
     */
    private String ocrRecordId;

    /**
     * 识别结果类型代码 1-百度AI自动识别 2-人工修改
     */
    private Integer resultType;

    /**
     * 患者ID
     */
    private String patientId;

    /**
     * 病案号
     */
    private String medicalNumber;

    /**
     * 患者姓名
     */
    private String name;

    /**
     * 患者年龄
     */
    private String age;

    /**
     * 患者性别
     */
    private String sex;

    /**
     * 临床诊断/表现
     */
    private String diagnose;

    /**
     * 医院名称
     */
    private String hospitalName;

    /**
     * 检验科室名称
     */
    private String deptName;

    /**
     * 检验项目名称
     */
    private String projectName;

    /**
     * 标本名称
     */
    private String specimenName;

    /**
     * 申请日期
     */
    private String applicationDate;

    /**
     * 采样日期
     */
    private String collectionDate;

    /**
     * 采样人姓名
     */
    private String collectionName;

    /**
     * 接收日期
     */
    private String receiveDate;

    /**
     * 报告日期
     */
    private String reportDate;

    /**
     * 申请人姓名
     */
    private String applicationName;

    /**
     * 检验者姓名
     */
    private String inspectionName;

    /**
     * 审核者姓名
     */
    private String reviewerName;

    /**
     * 报告人姓名
     */
    private String reporterName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 项目备注
     */
    private String projectComment;

    /**
     * 申请科室名称
     */
    private String applicationDept;

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
    private Date updateTime;

    private Integer offset;

    private Integer pagesize;


    // 441 add columns
    /**
     * 就诊类型代码
     */
    private String visitSerialTypeCode;
    /**
     * 就诊类型名称
     */
    private String visitSerialTypeName;
    /**
     * 就诊流水号
     */
    private String visitSerialNumber;
    // end add



    /********************* 非实体类字段 *****************************/
    private List<OcrInspectionResultItem> ocrInspectionResultItems;
}