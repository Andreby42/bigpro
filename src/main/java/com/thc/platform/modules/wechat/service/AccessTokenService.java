package com.thc.platform.modules.wechat.service;

import com.thc.platform.external.dto.AppConfigIn;
import com.thc.platform.external.dto.AppConfigOut;
import com.thc.platform.external.service.GlobalPlatformService;
import com.titan.wechat.common.api.basic.WxAccessToken;
import com.titan.wechat.common.api.basic.WxBasicApi;
import com.titan.wechat.common.api.basic.response.WxApiTicket;
import com.titan.wechat.common.api.exception.WxClientException;
import com.titan.wechat.common.api.open.WxOpenBasicApi;
import com.titan.wechat.common.api.open.response.OpenAccessTokenResponse;
import com.titan.wechat.common.api.open.response.OpenAuthorizerInfoResponse;
import com.titan.wechat.common.api.open.response.OpenAuthorizerTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description AccessToken处理
 * @Author ZWen
 * @Date 2019/11/30 3:26 PM
 * @Version 1.0
 **/
@Service
@Slf4j
public class AccessTokenService {

    private final WxRedisService wxRedisService;
    private final WxOpenBasicApi wxOpenBasicApi;
    private final WxBasicApi wxBasicApi;
    private final GlobalPlatformService globalPlatformService;

    public AccessTokenService(WxRedisService wxRedisService, WxOpenBasicApi wxOpenBasicApi, WxBasicApi wxBasicApi, GlobalPlatformService globalPlatformService) {
        this.wxRedisService = wxRedisService;
        this.wxOpenBasicApi = wxOpenBasicApi;
        this.wxBasicApi = wxBasicApi;
        this.globalPlatformService = globalPlatformService;
    }

    /**
     * 根据appId获取公众号或小程序的accessToken
     *
     * @param appId appId
     * @return accessToken
     */
    public String getAuthorizerAccessToken(final String appId) {
        String publicAccessToken = wxRedisService.getAuthorizerAccessToken(appId);
        if (StringUtils.isNotEmpty(publicAccessToken)) {
            return publicAccessToken;
        }
        try {
            //再次获取
            return refreshAuthorizerAccessToken(appId);
        } catch (WxClientException e) {
            log.info("获取accessToken失败，尝试根据appId和秘钥获取access_token,appId is [{}]", appId);
        }
        //如果获取失败，则根据appId和秘钥获取access_token并存储
        AppConfigIn appConfigIn = new AppConfigIn().setAppId(appId);
        appConfigIn.setPagesize(1);
        List<AppConfigOut> list = globalPlatformService.getTenantMiniAppConfig(appConfigIn);
        if (CollectionUtils.isEmpty(list)) {
            log.error("获取appId配置信息为空，appId is [{}]", appId);
        }
        String appSecret = list.get(0).getAppSecrete();
        WxAccessToken wxAccessToken = wxBasicApi.getAccessToken(appId, appSecret);
        log.info("getAccessToken by secret request is [{},{}] response is [{}]", appId, appSecret, wxAccessToken);
        wxAccessToken.checkSuccess();
        //保存到redis
        wxRedisService.setAndExpireAuthorizerAccessToken(wxAccessToken.getAccessToken(), appId);
        return wxAccessToken.getAccessToken();
    }

    /**
     * 根据appId获取公众号或小程序的ticket
     *
     * @param appId      公众号或小程序的appId
     * @param ticketType ticket类型
     * @return ticket
     */
    public String getAuthorizerTicket(final String appId, final WxApiTicket.TicketType ticketType) {
        //先从缓存中获取
        String ticket = wxRedisService.getAuthorizerTicket(appId, ticketType);
        if (StringUtils.isNotEmpty(ticket)) {
            return ticket;
        }
        String accessToken = getAuthorizerAccessToken(appId);
        WxApiTicket wxApiTicket = wxBasicApi.getApiTicket(accessToken, ticketType);
        log.info("获取对应appId的ticket appId[{}] response is : {}", appId, wxApiTicket);
        wxApiTicket.checkSuccess();
        //刷新缓存
        wxRedisService.setAndExpireAuthorizerTicket(wxApiTicket.getTicket(), appId, ticketType);
        return wxApiTicket.getTicket();
    }

    /**
     * 获取开放平台accessToken
     *
     * @return 开放平台accessToken
     */
    public String getOpenAccessToken() {
        String openAccessToken = wxRedisService.getOpenAccessToken();
        if (StringUtils.isNotEmpty(openAccessToken)) {
            return openAccessToken;
        }
        //获取用于刷新开放平台accessToken的票据(verifyTicket)
        String verifyTicket = wxRedisService.getOpenVerifyTicket();
        if (StringUtils.isNotEmpty(verifyTicket)) {
            return refreshOpenAccessToken(verifyTicket);
        }
        throw new WxClientException("开放平台verifyTicket为空");
    }

    /**
     * api获取开放平台accessToken，并刷新缓存
     *
     * @param verifyTicket
     * @return
     */
    public String refreshOpenAccessToken(final String verifyTicket) {
        OpenAccessTokenResponse token = wxOpenBasicApi.getOpenAccessToken(verifyTicket);
        token.checkSuccess();
        //刷新OpenAccessToken,并设置过期时间
        wxRedisService.setAndExpireOpenAccessToken(token.getComponentAccessToken());
        return token.getComponentAccessToken();
    }

    /**
     * 刷新公众号/小程序accessToken
     */
    public String refreshAuthorizerAccessToken(final String appId) {
        //从redis数据库中获取refreshToken
        final String refreshToken = wxRedisService.getAuthorizerRefreshToken(appId);
        log.info("刷新appId[{}] token is : {}", appId, refreshToken);
        final String accessToken = getOpenAccessToken();

        log.info("开始刷新 {} 公众号 AuthorizerAccessToken and AuthorizerRefreshToken", appId);
        OpenAuthorizerTokenResponse response = wxOpenBasicApi.authorizerToken(accessToken, appId, refreshToken);
        //如果响应RefreshToken过期，那么获取公众号基本信息，里面有新的RefreshToken(文档中没有写，自己发现的)
        if (response.getErrCode() == 61023) {
            log.info("刷新 {} 公众号 AuthorizerAccessToken时，refresh_token过期,{}", appId, response);
            OpenAuthorizerInfoResponse authorizerInfo = wxOpenBasicApi.authorizerInfo(accessToken, appId);
            if (authorizerInfo.getErrCode() == 0) {
                response = wxOpenBasicApi.authorizerToken(accessToken, appId, authorizerInfo.getAuthorizationInfo().getAuthorizerRefreshToken());
            }
        }
        if (response.getErrCode() == 0) {
            //将accessToken保存到redis中，并设置过期时间
            wxRedisService.setAndExpireAuthorizerAccessToken(response.getAccessToken(), appId);
            log.info("刷新完成 appId {} AuthorizerAccessToken {}", appId, response);
            //如果AuthorizerRefreshToken和数据库中的不一致，更新
            if (!response.getRefreshToken().equals(refreshToken)) {
                log.info("refreshToken 不一致,刷新redis AuthorizerRefreshToken,{}", response);
                wxRedisService.setAuthorizerRefreshToken(response.getRefreshToken(), appId);
            }
            return response.getAccessToken();
        } else {
            log.error("刷新失败 {} 公众号 AuthorizerAccessToken and AuthorizerRefreshToken 失败,authorizerToken error: {}", appId, response);
            throw new WxClientException("刷新公众号token失败，appId is " + appId);
        }
    }
}