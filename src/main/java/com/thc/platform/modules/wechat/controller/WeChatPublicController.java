package com.thc.platform.modules.wechat.controller;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.wechat.constant.WeChatConstant;
import com.thc.platform.modules.wechat.dto.PublicLoginInfoOut;
import com.thc.platform.modules.wechat.service.WeChatAppInfoService;
import com.thc.platform.modules.wechat.service.WeChatPublicService;
import com.titan.wechat.common.api.basic.WxUserInfo;
import com.titan.wechat.common.api.basic.request.QrCodeCreateRequest;
import com.titan.wechat.common.api.basic.response.JsConfigResp;
import com.titan.wechat.common.api.basic.response.QrCodeCreateResponse;
import com.titan.wechat.common.api.business.dto.AppDetailInfoResp;
import com.titan.wechat.common.api.business.dto.CreateUnAuthAppIdInfoIn;
import com.titan.wechat.common.api.business.dto.TemplateMsgSendIn;
import com.titan.wechat.common.api.open.response.OpenOauth2AccessTokenResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description 微信公众号相关第三方接口
 * @Author ZWen
 * @Date 2018/12/30 2:59 PM
 * @Version 1.0
 **/
@io.swagger.annotations.Api(tags = "微信-公众号")
@Slf4j
@RestController
@RequestMapping(WeChatConstant.PATH_PREFIX + "/public")
public class WeChatPublicController {

    private final WeChatPublicService weChatPublicService;
    private final WeChatAppInfoService weChatAppInfoService;

    public WeChatPublicController(WeChatPublicService weChatPublicService, WeChatAppInfoService weChatAppInfoService) {
        this.weChatPublicService = weChatPublicService;
        this.weChatAppInfoService = weChatAppInfoService;
    }

    @ApiOperation("发送模板消息")
    @PostMapping("/sendTemplateMsg")
    public Api sendTemplateMsg(@RequestBody TemplateMsgSendIn templateMsgSendIn) {
        templateMsgSendIn.checkParams();
        log.info("发送模板消息 request : [{}]", JSON.toJSONString(templateMsgSendIn));
        weChatPublicService.send(templateMsgSendIn);
        return Api.SUCCESS_RESULT;
    }

    @ApiOperation("生成公众号的带参二维码")
    @PostMapping("/getQrCode/{appId}")
    public Api<QrCodeCreateResponse> qrCodeCreate(@PathVariable String appId, @RequestBody QrCodeCreateRequest qrCodeCreateRequest) {
        return Api.ok(weChatPublicService.qrCodeCreate(qrCodeCreateRequest, appId));
    }

    @ApiOperation("通过 code和appId 换取 openId和access_token")
    @GetMapping("/getOpenIdByCodeAndAppId")
    public Api<OpenOauth2AccessTokenResponse> getOpenIdByCodeAndAppId(@RequestParam String appId, @RequestParam String code) {
        return Api.ok(weChatPublicService.getOpenIdByCodeAndAppId(appId, code));
    }

    @ApiOperation("通过 code和appId 换取 用户基本信息")
    @GetMapping("/getUserInfoByCodeAndAppId")
    public Api<WxUserInfo> getUserInfoByCodeAndAppId(@RequestParam String appId, @RequestParam String code) {
        return Api.ok(weChatPublicService.getUserInfoByCodeAndAppId(appId, code));
    }

    @ApiOperation("创建租户待授权微信公众号配置信息")
    @PostMapping("/createUnAuthAppIdInfo")
    public Api createUnAuthAppIdInfo(@RequestBody CreateUnAuthAppIdInfoIn in) {
        weChatAppInfoService.createUnAuthAppIdInfo(in);
        return Api.SUCCESS_RESULT;
    }

    @ApiOperation("获取授权方appId对应的账号信息和授权信息")
    @PostMapping("/findDetailByAppIdAndTenantId")
    public Api<AppDetailInfoResp> findDetailByAppId(@RequestParam("appId") String appId, @RequestParam("tenantId") Integer tenantId) {
        return Api.ok(weChatAppInfoService.findDetailByAppId(appId, tenantId));
    }

    @ApiOperation("获取租户已授权公众号列表")
    @PostMapping("/listAuthPubAppByTenantId")
    public Api<List<AppDetailInfoResp>> listAuthPubAppByTenantId(@RequestParam("tenantId") Integer tenantId) {
        return Api.ok(weChatAppInfoService.listAuthPubAppByTenantId(tenantId));
    }

    @ApiOperation("获取公众号代开发管理员授权地址")
    @GetMapping("/getAuthUrl")
    public Api<String> getAuthUrl(@RequestParam String appId, @RequestParam Integer typeCode) {
        return Api.ok(weChatAppInfoService.getAuthUrl(appId, typeCode));
    }

    @ApiOperation(value = "获取微信网页js.config")
    @GetMapping(value = "/jsConfig")
    public Api<JsConfigResp> getJsConfig(@RequestParam String url, @RequestParam String appId) {
        return Api.ok(weChatPublicService.getJsConfig(url, appId));
    }

    @ApiOperation(value = "获取租户对应商城H5所需访问的接口的域名")
    @GetMapping(value = "/mallApiDomain/{tenantId}")
    public Api<String> mallApiDomain(@PathVariable Integer tenantId) {
        return Api.ok(weChatPublicService.getTenantConfigByTenantId(tenantId).getTitanHost());
    }

    @ApiOperation(value = "获取租户互联网域名及微信登录appId")
    @GetMapping(value = "/domainAndAppId/{tenantId}")
    public Api<PublicLoginInfoOut> domainAndAppId(@PathVariable Integer tenantId) {
        return Api.ok(weChatPublicService.domainAndAppId(tenantId));
    }
}