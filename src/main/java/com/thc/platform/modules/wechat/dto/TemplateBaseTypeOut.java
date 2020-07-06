package com.thc.platform.modules.wechat.dto;

import com.titan.wechat.common.api.business.enums.TemplateBaseTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/12/2 6:35 PM
 * @Version 1.0
 **/
@Data
@ApiModel("基础模板列表")
public class TemplateBaseTypeOut {

    @ApiModelProperty("模板类型标志")
    private TemplateBaseTypeEnum templateBaseType;

    @ApiModelProperty("模板标题")
    private final String title;

    @ApiModelProperty("模板每行的标题")
    private final TemplateBaseTypeEnum.TemplateData data;

    public TemplateBaseTypeOut(TemplateBaseTypeEnum templateBaseTypeEnum) {
        this.templateBaseType = templateBaseTypeEnum;
        this.title = templateBaseTypeEnum.getTitle();
        this.data = templateBaseTypeEnum.getData();
    }
}