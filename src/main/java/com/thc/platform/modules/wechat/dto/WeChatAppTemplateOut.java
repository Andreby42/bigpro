package com.thc.platform.modules.wechat.dto;

import com.thc.platform.modules.wechat.entity.WeChatAppTemplateEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/12/2 3:03 PM
 * @Version 1.0
 **/
@Data
@ApiModel("公众号模板响应信息")
public class WeChatAppTemplateOut {

    @ApiModelProperty("id")
    private String id;

    @ApiModelProperty("模板标题")
    private String title;

    @ApiModelProperty("模板id")
    private String templateId;

    @ApiModelProperty("appId")
    private String appId;

    public WeChatAppTemplateOut(WeChatAppTemplateEntity e) {
        this.id = e.getId();
        this.title = e.getTitle();
        this.templateId = e.getTemplateId();
        this.appId = e.getAppId();
    }
}