package com.thc.platform.modules.wechat.handler.openmsg;

import com.thc.platform.modules.wechat.service.AccessTokenService;
import com.thc.platform.modules.wechat.service.WeChatAppInfoService;
import com.thc.platform.modules.wechat.service.WxRedisService;
import com.titan.wechat.common.api.open.OpenMsgHandler;
import com.titan.wechat.common.api.open.OpenReply;
import com.titan.wechat.common.api.open.WxOpenBasicApi;
import com.titan.wechat.common.api.open.event.received.OpenUpdateAuthorizedMsg;
import com.titan.wechat.common.api.open.response.AuthorizationInfo;
import com.titan.wechat.common.api.open.response.OpenQueryAuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description 授权更新成功处理
 * @Author ZWen
 * @Date 2018/12/30 8:46 PM
 * @Version 1.0
 **/
@Component
public class OpenUpdateAuthorizedMsgHandler implements OpenMsgHandler<OpenUpdateAuthorizedMsg> {

    private static final Logger logger = LoggerFactory.getLogger(OpenUpdateAuthorizedMsgHandler.class);

    private final WxRedisService wxRedisService;
    private final WxOpenBasicApi wxOpenBasicApi;
    private final AccessTokenService accessTokenService;
    private final WeChatAppInfoService weChatAppInfoService;

    public OpenUpdateAuthorizedMsgHandler(WxRedisService wxRedisService, WxOpenBasicApi wxOpenBasicApi, AccessTokenService accessTokenService, WeChatAppInfoService weChatAppInfoService) {
        this.wxRedisService = wxRedisService;
        this.wxOpenBasicApi = wxOpenBasicApi;
        this.accessTokenService = accessTokenService;
        this.weChatAppInfoService = weChatAppInfoService;
    }

    @Override
    public OpenReply process(OpenUpdateAuthorizedMsg msg) {
        String authorizationCode = msg.getAuthorizationCode();
        String openAccessToken = accessTokenService.getOpenAccessToken();
        OpenQueryAuthResponse queryAuth = wxOpenBasicApi.queryAuth(openAccessToken, authorizationCode);
        if (queryAuth.getErrCode() != 0) {
            logger.error("授权更新通知处理失败 queryAuth error: {}", queryAuth);
            return OpenReply.FAIL_REPLY;
        }
        //刷新授权方appId的accessToken和refreshToken并保存
        logger.info("授权更新通知处理成功 queryAuth: {}", queryAuth);
        AuthorizationInfo info = queryAuth.getAuthorizationInfo();
        //将refreshToken保存到redis
        wxRedisService.setAuthorizerRefreshToken(info.getRefreshToken(), info.getAppId());
        //将accessToken保存到redis，并设置过期时间
        wxRedisService.setAndExpireAuthorizerAccessToken(info.getAccessToken(), info.getAppId());
        //更新到数据库
        weChatAppInfoService.saveByAuth(info);
        return OpenReply.SUCCESS_REPLY;
    }

    @Override
    public Class<OpenUpdateAuthorizedMsg> getMsgClass() {
        return OpenUpdateAuthorizedMsg.class;
    }
}