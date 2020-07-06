package com.thc.platform.modules.wechat.service;

import com.titan.wechat.common.api.mini.WxMiniApi;
import com.titan.wechat.common.api.mini.req.GetMiniCodeIn;
import com.titan.wechat.common.api.mini.req.GetUnlimitMiniCodeIn;
import com.titan.wechat.common.api.support.WxStreamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/11/18 10:38 AM
 * @Version 1.0
 **/
@Service
@Slf4j
public class WeChatMiniService {

    private final WxMiniApi wxMiniApi;
    private final AccessTokenService accessTokenService;

    public WeChatMiniService(WxMiniApi wxMiniApi, AccessTokenService accessTokenService) {
        this.wxMiniApi = wxMiniApi;
        this.accessTokenService = accessTokenService;
    }

    /**
     * 生成小程序码(有限制)
     *
     * @param appId
     * @param getMiniCodeIn
     * @return 返回图片响应流
     */
    public WxStreamResponse getMiniCodeImgLink(final String appId, GetMiniCodeIn getMiniCodeIn) {
        String accessToken = accessTokenService.getAuthorizerAccessToken(appId);
        return  wxMiniApi.getwxacode(getMiniCodeIn, accessToken);
    }

    /**
     * 生成小程序码(无限制)
     *
     * @param appId
     * @param getUnlimitMiniCodeIn
     * @return 返回图片响应流
     */
    public WxStreamResponse getUnlimitMiniCodeImgLink(final String appId, GetUnlimitMiniCodeIn getUnlimitMiniCodeIn) {
        String accessToken = accessTokenService.getAuthorizerAccessToken(appId);
        return wxMiniApi.getwxacodeunlimit(getUnlimitMiniCodeIn, accessToken);
    }
}