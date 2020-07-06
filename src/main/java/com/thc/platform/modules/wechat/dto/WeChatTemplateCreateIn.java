package com.thc.platform.modules.wechat.dto;

import com.titan.common.util.FieldChecker;
import com.titan.wechat.common.api.business.enums.TemplateBaseTypeEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/12/2 5:11 PM
 * @Version 1.0
 **/
@Data
@Accessors(chain = true)
@ApiModel("创建模板入参")
public class WeChatTemplateCreateIn {

    @ApiModelProperty("公众号appId")
    private String appId;

    @ApiModelProperty("基础模板类型标志")
    private TemplateBaseTypeEnum templateBaseType;

    public void validate() {
        FieldChecker.assertNotEmpty(this.appId, "公众号appId不能为空");
        FieldChecker.assertNotNull(this.templateBaseType, "基础模板类型不能为空");
    }
}