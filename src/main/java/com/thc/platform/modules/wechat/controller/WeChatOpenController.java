package com.thc.platform.modules.wechat.controller;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.wechat.config.ApiConfig;
import com.thc.platform.modules.wechat.constant.WeChatConstant;
import com.thc.platform.modules.wechat.dto.AppInfoByOrgAndTypeOut;
import com.thc.platform.modules.wechat.service.AccessTokenService;
import com.thc.platform.modules.wechat.service.WeChatAppInfoService;
import com.thc.platform.modules.wechat.service.WxRedisService;
import com.titan.wechat.common.api.basic.response.WxApiTicket;
import com.titan.wechat.common.api.exception.AesException;
import com.titan.wechat.common.api.message.WxMsgService;
import com.titan.wechat.common.api.message.WxReply;
import com.titan.wechat.common.api.open.OpenMsgService;
import com.titan.wechat.common.api.open.OpenReply;
import com.titan.wechat.common.api.open.WxOpenBasicApi;
import com.titan.wechat.common.api.open.response.CreateOpenAndBindResponse;
import com.titan.wechat.common.api.support.WxResponse;
import com.titan.wechat.common.api.util.WeChatConfigUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description 第三方平台相关接口
 * 运维配置线上第三放平台ip白名单
 * 业务域名也需要配置(网页开发用)，商城前端代码需要在global域名下，因为第三方平台只可以配置三个域名，如果有4个互联网医院池子，没法做
 * <p>
 * 官方api文档：https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/Third_party_platform_appid.html
 * @Author ZWen
 * @Date 2019/12/30 2:59 PM
 * @Version 1.0
 **/
@io.swagger.annotations.Api(tags = "微信-第三方平台")
@RestController
@RequestMapping(WeChatConstant.PATH_PREFIX + "/open")
public class WeChatOpenController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatOpenController.class);

    private final ApiConfig.ComponentProperties componentProperties;
    private final OpenMsgService openMsgService;
    private final WxMsgService wxMsgService;
    private final AccessTokenService accessTokenService;
    private final WeChatAppInfoService weChatAppInfoService;
    private final WxOpenBasicApi wxOpenBasicApi;
    private final WxRedisService wxRedisService;


    public WeChatOpenController(ApiConfig.ComponentProperties componentProperties, OpenMsgService openMsgService, WxMsgService wxMsgService, AccessTokenService accessTokenService, WeChatAppInfoService weChatAppInfoService, WxOpenBasicApi wxOpenBasicApi, WxRedisService wxRedisService) {
        this.componentProperties = componentProperties;
        this.openMsgService = openMsgService;
        this.wxMsgService = wxMsgService;
        this.accessTokenService = accessTokenService;
        this.weChatAppInfoService = weChatAppInfoService;
        this.wxOpenBasicApi = wxOpenBasicApi;
        this.wxRedisService = wxRedisService;
    }

    /**
     * 第三方平台 授权事件接收
     *
     * @param xml          消息体
     * @param msgSignature 签名
     * @param timestamp    时间戳
     * @param nonce        随机字符串
     * @return
     */
    @ApiIgnore
    @RequestMapping("callbackAuth")
    public String auth(@RequestBody String xml, @RequestParam("msg_signature") String msgSignature, String signature, String timestamp, String nonce) throws AesException {
        logger.info("授权事件接收 auth params msg_signature:{},signature:{},timestamp:{},nonce:{}, xml:\n{}", msgSignature, signature, timestamp, nonce, xml);
        String replyContent = WeChatConfigUtil.decryptMsgAppId(msgSignature, timestamp, nonce, xml, componentProperties.getToken(), componentProperties.getAppId(), componentProperties.getAesKey());
        logger.info("auth xml 解密 is:\n{}", replyContent);
        OpenReply reply = openMsgService.process(replyContent);
        return reply.getContent();
    }

    /**
     * 管理的公众号的消息和事件推送
     *
     * @param appId        公众号appId
     * @param xml          消息体
     * @param msgSignature 签名
     * @param timestamp    时间戳
     * @param nonce        随机字符串
     * @return
     * @throws AesException
     */
    @ApiIgnore
    @PostMapping("callbackMsg/{appId}")
    public String receiveMsg(@PathVariable String appId, @RequestBody String xml, @RequestParam("msg_signature") String msgSignature, String timestamp, String nonce) throws AesException {
        logger.debug("第三方平台-公众号推送-密文 appId:{},xml:\n{}", appId, xml);
        String replyContent = WeChatConfigUtil.decryptMsg(msgSignature, timestamp, nonce, xml, componentProperties.getToken(), componentProperties.getAppId(), componentProperties.getAesKey());
        logger.info("第三方平台-公众号推送-解密 is:\n{}", replyContent);
        //下面这个的意思是 过滤掉在模版消息发送任务完成后，微信服务器推送的是否送达成功的通知
        if (!replyContent.contains("TEMPLATESENDJOBFINISH")) {
            WxReply reply = wxMsgService.process(replyContent, appId);
            return reply.getContent();
//            if (reply != null) {
//                logger.debug("第三方平台-公众号推送-回复 明文 is:\n{}", reply.getContent());
//                replyContent = WeChatConfigUtil.encryptMsg(reply.getContent(), timestamp, nonce, appId, componentProperties.getAesKey(), componentProperties.getToken());
//                logger.debug("第三方平台-公众号推送-回复 加密 is:\n{}", replyContent);
//            }
        }
        return "success";
    }

    /**
     * 授权回调
     *
     * @param authCode  授权码
     * @param expiresIn 过期时间
     * @return
     */
    @ApiIgnore
    @GetMapping("/authSuccess")
    public String authBack(@RequestParam("auth_code") String authCode, @RequestParam("expires_in") Integer expiresIn) {
        logger.info("授权回调 auth_code is:{},expires_in is:{}", authCode, expiresIn);
        return "success";
    }

    /**
     * 手动刷新某个公众号的authorizer_access_token
     *
     * @return
     */
    @ApiIgnore
    @GetMapping("/refreshAuthorizerToken/{appId}")
    public Api refreshToken(@PathVariable String appId) {
        accessTokenService.refreshAuthorizerAccessToken(appId);
        return Api.SUCCESS_RESULT;
    }

    /**
     * 创建开放平台帐号并绑定公众号/小程序
     *
     * @return
     */
    @ApiIgnore
    @GetMapping("/createOpenAndBind")
    public Api createOpenAndBind(@RequestParam String appId) {
        String accessToken = accessTokenService.getAuthorizerAccessToken(appId);
        CreateOpenAndBindResponse response = wxOpenBasicApi.createOpenAndBind(appId, accessToken);
        logger.info("createOpenAndBind appId : [{}] response : [{}]", appId, response);
        return Api.ok(response);
    }

    @ApiIgnore
    @ApiOperation("获取公众号/小程序所绑定的开放平台帐号")
    @GetMapping("/getOpenByAppId")
    public Api getOpenByAppId(@RequestParam String appId) {
        CreateOpenAndBindResponse response = wxOpenBasicApi.getOpenByAppId(appId, accessTokenService.getAuthorizerAccessToken(appId));
        logger.info("getOpenByAppId appId : [{}] response : [{}]", appId, response);
        return Api.ok(response);
    }

    @ApiIgnore
    @ApiOperation("将公众号/小程序绑定到开放平台帐号下")
    @GetMapping("/bindAppIdToOpen")
    public Api bindAppIdToOpen(@RequestParam String appId, @RequestParam String openAppId) {
        WxResponse response = wxOpenBasicApi.bindAppIdToOpen(appId, openAppId, accessTokenService.getAuthorizerAccessToken(appId));
        logger.info("bindAppIdToOpen appId : [{}] openAppId : [{}] response : [{}]", appId, openAppId, response);
        return Api.ok(response);
    }

    @ApiIgnore
    @ApiOperation("将公众号/小程序从开放平台帐号下解绑")
    @GetMapping("/unBindAppIdToOpen")
    public Api unBindAppIdToOpen(@RequestParam String appId, @RequestParam String openAppId) {
        WxResponse response = wxOpenBasicApi.unBindAppIdToOpen(appId, openAppId, accessTokenService.getAuthorizerAccessToken(appId));
        logger.info("unBindAppIdToOpen appId : [{}] openAppId : [{}] response : [{}]", appId, openAppId, response);
        return Api.ok(response);
    }

    @ApiOperation("判断租户是否有已授权的公众号")
    @GetMapping("/checkPublicApp")
    public Api checkPublicApp(@ApiParam("租户id") @RequestParam Integer tenantId) {
        weChatAppInfoService.checkPublicApp(tenantId);
        return Api.SUCCESS_RESULT;
    }

    @ApiOperation("获取机构下小程序或公众号appId及第三方平台appId")
    @GetMapping("/getAppIdByOrgIdAndType")
    public Api<AppInfoByOrgAndTypeOut> getAppIdByOrgIdAndType(@ApiParam("租户id") @RequestParam Integer tenantId, @ApiParam("机构id") @RequestParam String orgId, @ApiParam("1微信公众号;2小程序") @RequestParam Integer appType) {
        return Api.ok(weChatAppInfoService.getAppIdByOrgIdAndType(tenantId, orgId, appType));
    }

    @ApiOperation("根据appId获取公众号或小程序的ticket")
    @GetMapping("/getTicket/{appId}")
    public Api<String> getTicket(@PathVariable String appId, @RequestParam WxApiTicket.TicketType ticketType) {
        return Api.ok(accessTokenService.getAuthorizerTicket(appId, ticketType));
    }

    @ApiOperation("获取第三方平台appId")
    @GetMapping("/getOpenAppId")
    public Api<String> getOpenAppId() {
        return Api.ok(componentProperties.getAppId());
    }

    @ApiIgnore
    @GetMapping("/delWeChatRedisByPrefix")
    public Api<Long> delWeChatRedisByPrefix(@RequestParam String prefix) {
        return Api.ok(wxRedisService.delWeChatRedisByPrefix(prefix));
    }
}