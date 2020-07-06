package com.thc.platform.external.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/11/18 3:22 PM
 * @Version 1.0
 **/
@Data
@ApiModel("APP配置信息查询入参")
@Accessors(chain = true)
public class AppConfigIn {

    @ApiModelProperty(value = "偏移量", example = "0")
    private Integer offset = 0;
    @ApiModelProperty(value = "每页显示条数", example = "10")
    private Integer pagesize = 10;

    @ApiModelProperty("租户Id")
    private String tenantId;

    @ApiModelProperty("1微信公众号;2小程序")
    private Integer typeCode;

    @ApiModelProperty("appId")
    private String appId;
}