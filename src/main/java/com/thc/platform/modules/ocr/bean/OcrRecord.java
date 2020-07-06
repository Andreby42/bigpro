package com.thc.platform.modules.ocr.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**                                                        
 * @Description: OcrRecord           
 *                                                          
 ********************************************************   
 * @author                 @date             @version       
 * zouyu            2020-02-01         1.0.0          
 ********************************************************   
 */

@Setter
@Getter
@ToString
public class OcrRecord {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 租户名称
     */
    private String tenantName;

    /**
     * 机构ID
     */
    private String orgId;

    /**
     * 机构名称
     */
    private String orgName;

    /**
     * 上传文件地址
     */
    private String url;

    /**
     * 图片来源类型 1-THC 2-互联网医院
     */
    private Integer sourceType;

    /**
     * 识别图片类型 1-检验 2-检查
     */
    private Integer type;

    /**
     * 识别结果代码 1-成功 2-失败
     */
    private Integer result;

    /**
     * 匹配模板ID
     */
    private String templateSign;

    /**
     * 匹配模板名称
     */
    private String templateName;

    /**
     * 分数
     */
    private String score;

    /**
     * 失败原因
     */
    private String errorMsg;

    /**
     * 操作人员类型 1-医生 2-患者
     */
    private Integer operatorType;

    /**
     * 操作人员ID
     */
    private String operatorId;

    /**
     * 操作人员姓名
     */
    private String operatorName;

    /**
     * 操作日期
     */
    private Date operationDate;

    /**
     * 创建人ID
     */
    private String creator;

    /**
     * 创建人姓名
     */
    private String creatorName;

    /**
     * 修改人ID
     */
    private String updater;

    /**
     * 修改人姓名
     */
    private String updaterName;

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
    private String patientId;
    private String patientName;
    private OcrInspectionResult ocrInspectionResult;
    private OcrExamineResult ocrExamineResult;
    private Date operationStartDate;
    private Date operationEndDate;
    private String fileId;
    private String localFilePath;
    private String token;

//    private List<OcrInspectionResult> ocrInspectionResults;
//    private List<OcrExamineResult> ocrExamineResults;
}