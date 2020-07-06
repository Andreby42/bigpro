package com.thc.platform.external.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/11/18 3:22 PM
 * @Version 1.0
 **/
@Data
@ApiModel("APP配置信息响应")
@Accessors(chain = true)
public class AppConfigOut implements Serializable {

    private static final long serialVersionUID = 42L;

    @ApiModelProperty("租户Id")
    private Integer tenantId;

    @ApiModelProperty("机构Id")
    private String orgId;

    @ApiModelProperty("1微信公众号;2小程序")
    private Integer typeCode;

    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("秘钥")
    private String appSecrete;

    @ApiModelProperty("名称")
    private String appName;
}