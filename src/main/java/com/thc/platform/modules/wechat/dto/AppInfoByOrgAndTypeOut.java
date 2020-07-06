package com.thc.platform.modules.wechat.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author ZWen
 * @Date 2020/1/2 3:00 PM
 * @Version 1.0
 **/
@Data
@ApiModel("小程序或公众号appId及第三方平台appId响应")
@Accessors(chain = true)
public class AppInfoByOrgAndTypeOut {

    @ApiModelProperty("公众号或小程序appId")
    private String appId;

    @ApiModelProperty("第三方平台appId")
    private String componentAppId;
}