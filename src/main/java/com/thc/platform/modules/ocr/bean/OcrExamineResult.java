package com.thc.platform.modules.ocr.bean;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author @date             @version
 * zouyu            2020-02-01         1.0.0
 * *******************************************************
 * @Description: OcrExamineResult
 * <p>
 * *******************************************************
 */

@Setter
@Getter
public class OcrExamineResult {

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
     * 检查科室名称
     */
    private String deptName;

    /**
     * 检查项目名称
     */
    private String projectName;

    /**
     * 标本名称
     */
    private String specimenName;

    /**
     * 申请科室名称
     */
    private String applicationDept;

    /**
     * 送检医师姓名
     */
    private String givenName;

    /**
     * 报告日期
     */
    private String reportDate;

    /**
     * 审核医师姓名
     */
    private String reviewerName;

    /**
     * 报告医师姓名
     */
    private String reporterName;

    /**
     * 检查医师姓名
     */
    private String examinerName;

    /**
     * 检查日期
     */
    private String examineDate;

    /**
     * 检查部位
     */
    private String examinePosition;

    /**
     * 检查方法
     */
    private String examineMethod;

    /**
     * 检查所见
     */
    private String examineFindings;

    /**
     * 检查印象
     */
    private String examineImpression;

    /**
     * 检查建议
     */
    private String examineSuggestion;

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

}