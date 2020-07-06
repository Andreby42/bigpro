package com.thc.platform.external.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @Description
 * @Author jyf
 * @Date 2019/10/29 6:38 PM
 * @Version 1.0
 **/
@ApiModel("互联网医院配置")
@Data
@Accessors(chain = true)
public class InternetHospitalConfigDto implements Serializable {

    private static final long serialVersionUID = 42L;

    @ApiModelProperty("租户ID")
    private Integer tenantId;

    @ApiModelProperty("商城业务开关")
    private String storeBusiSwitch;

    @ApiModelProperty("院外发药开关")
    private String externalDispensingSwitch;

    @ApiModelProperty("关联互联网资质租户ID")
    private Integer internetCredentialsTenantId;
}