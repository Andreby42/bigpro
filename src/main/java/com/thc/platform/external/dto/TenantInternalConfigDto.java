package com.thc.platform.external.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @Description
 * @Author jyf
 * @Date 2019/10/29 6:42 PM
 * @Version 1.0
 **/
@ApiModel("租户互联网相关配置信息")
@Data
@Accessors(chain = true)
public class TenantInternalConfigDto implements Serializable {

    private static final long serialVersionUID = 42L;

    @ApiModelProperty("集团名称")
    private String groupName;

    @ApiModelProperty("集团域名")
    private String groupDomainName;

    @ApiModelProperty("集团全域名")
    private String fullGroupDomainName;

    @ApiModelProperty("医疗机构代码")
    private String medicalInstitutionCode;

    @ApiModelProperty("租户ID")
    private Integer tenantId;

    @ApiModelProperty("互联网医院配置")
    private InternetHospitalConfigDto internetHospitalConfig;

    @ApiModelProperty("集团额外全域名")
    private List<String> domainNames;

    @ApiModelProperty("环境配置信息")
    private List<EnvInfo> envInfos;

    private String titanHost;

    public String findHttpFullGroupDomainName(){

        if(StringUtils.isEmpty(this.fullGroupDomainName)){
            return null;
        }
        if (!this.fullGroupDomainName.startsWith("http")) {
            return  "http://" +this.fullGroupDomainName;
        }else {
            return this.fullGroupDomainName;
        }
    }

    @Data
    public static class EnvInfo implements Serializable {

        private static final long serialVersionUID = 42L;

        @ApiModelProperty("环境id")
        private String envId;

        @ApiModelProperty("环境类型")
        private String appType;
    }
}