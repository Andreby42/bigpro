package com.thc.platform.modules.wechat.handler.openmsg;

import com.thc.platform.modules.wechat.service.AccessTokenService;
import com.thc.platform.modules.wechat.service.WeChatAppInfoService;
import com.thc.platform.modules.wechat.service.WxRedisService;
import com.titan.wechat.common.api.open.OpenMsgHandler;
import com.titan.wechat.common.api.open.OpenReply;
import com.titan.wechat.common.api.open.WxOpenBasicApi;
import com.titan.wechat.common.api.open.event.received.OpenAuthorizedMsg;
import com.titan.wechat.common.api.open.response.AuthorizationInfo;
import com.titan.wechat.common.api.open.response.OpenQueryAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description 公众号授权成功处理
 * @Author ZWen
 * @Date 2018/12/30 8:46 PM
 * @Version 1.0
 **/
@Component
public class OpenAuthorizedMsgHandler implements OpenMsgHandler<OpenAuthorizedMsg> {

    private static final Logger logger = LoggerFactory.getLogger(OpenAuthorizedMsgHandler.class);

    private final AccessTokenService accessTokenService;
    private final WxOpenBasicApi wxOpenBasicApi;
    private final WxRedisService wxRedisService;
    private final WeChatAppInfoService weChatAppInfoService;

    public OpenAuthorizedMsgHandler(AccessTokenService accessTokenService, WxOpenBasicApi wxOpenBasicApi, WxRedisService wxRedisService, WeChatAppInfoService weChatAppInfoService) {
        this.accessTokenService = accessTokenService;
        this.wxOpenBasicApi = wxOpenBasicApi;
        this.wxRedisService = wxRedisService;
        this.weChatAppInfoService = weChatAppInfoService;
    }

    @Override
    public OpenReply process(OpenAuthorizedMsg msg) {
        String authorizationCode = msg.getAuthorizationCode();
        String openAccessToken = accessTokenService.getOpenAccessToken();
        OpenQueryAuthResponse queryAuth = wxOpenBasicApi.queryAuth(openAccessToken, authorizationCode);
        if (queryAuth.getErrCode() != 0) {
            logger.error("授权码换取公众号的授权信息失败 queryAuth: {}", queryAuth);
            return OpenReply.FAIL_REPLY;
        }
        logger.info("授权码换取公众号的授权信息成功 queryAuth: {}", queryAuth);
        //刷新授权方appId的accessToken和refreshToken并保存
        AuthorizationInfo info = queryAuth.getAuthorizationInfo();
        //将refreshToken保存到redis
        wxRedisService.setAuthorizerRefreshToken(info.getRefreshToken(), info.getAppId());
        //将accessToken保存到redis，并设置过期时间
        wxRedisService.setAndExpireAuthorizerAccessToken(info.getAccessToken(), info.getAppId());
        //更新到数据库
        weChatAppInfoService.saveByAuth(info);
        //todo:是否自动生成该公众号的所需模板列表待定
        return OpenReply.SUCCESS_REPLY;
    }

    @Override
    public Class<OpenAuthorizedMsg> getMsgClass() {
        return OpenAuthorizedMsg.class;
    }
}