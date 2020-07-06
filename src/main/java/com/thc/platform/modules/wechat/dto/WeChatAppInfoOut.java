package com.thc.platform.modules.wechat.dto;

import com.thc.platform.modules.wechat.constant.WeChatEnum;
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
@ApiModel("app配置响应信息")
public class WeChatAppInfoOut {

    @ApiModelProperty("app名称")
    private String appName;

    @ApiModelProperty("appId")
    private String appId;

    @ApiModelProperty("1微信公众号;2小程序")
    private Integer typeCode;

    @ApiModelProperty("授权状态 0=待授权 1=已授权 2=已取消授权")
    private Integer status = WeChatEnum.AppAuthStatus.NO.getCode();

    @ApiModelProperty("授权给开发者的权限id集合,https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/api/func_info.html")
    private String funcInfo = "";
}