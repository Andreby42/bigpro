package com.thc.platform.modules.wechat.service;

import com.alibaba.fastjson.JSONObject;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.external.api.GlobalPlatformApi;
import com.thc.platform.external.dto.AppConfigIn;
import com.thc.platform.external.dto.AppConfigOut;
import com.thc.platform.external.dto.TenantInternalConfigDto;
import com.thc.platform.modules.wechat.constant.WeChatConstant;
import com.thc.platform.modules.wechat.constant.WeChatEnum;
import com.thc.platform.modules.wechat.dto.AppInfoByOrgAndTypeOut;
import com.thc.platform.modules.wechat.dto.PublicLoginInfoOut;
import com.titan.common.constant.HeaderConstant;
import com.titan.common.result.ResultVO;
import com.titan.wechat.common.api.basic.WxBasicApi;
import com.titan.wechat.common.api.basic.WxUserInfo;
import com.titan.wechat.common.api.basic.request.QrCodeCreateRequest;
import com.titan.wechat.common.api.basic.response.JsConfigResp;
import com.titan.wechat.common.api.basic.response.QrCodeCreateResponse;
import com.titan.wechat.common.api.basic.response.WxApiTicket;
import com.titan.wechat.common.api.business.dto.TemplateMsgSendIn;
import com.titan.wechat.common.api.business.dto.publicmsg.PublicEventMsgRequest;
import com.titan.wechat.common.api.message.WxMessageApi;
import com.titan.wechat.common.api.message.send.template.TemplateMsgRequest;
import com.titan.wechat.common.api.message.send.template.TemplateMsgResponse;
import com.titan.wechat.common.api.open.WxOpenBasicApi;
import com.titan.wechat.common.api.open.response.OpenOauth2AccessTokenResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Description 公众号相关三方接口service
 * @Author ZWen
 * @Date 2019/12/3 11:36 AM
 * @Version 1.0
 **/
@Slf4j
@Service
public class WeChatPublicService {

    private final AccessTokenService accessTokenService;
    private final WxMessageApi wxMessageApi;
    private final WeChatAppTemplateService weChatAppTemplateService;
    private final GlobalPlatformApi globalPlatformApi;
    private final WxRedisService wxRedisService;
    private final RestTemplate restTemplate;
    private final WxBasicApi wxBasicApi;
    private final WxOpenBasicApi wxOpenBasicApi;
    private final WeChatAppInfoService weChatAppInfoService;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public WeChatPublicService(AccessTokenService accessTokenService, WxMessageApi wxMessageApi, WeChatAppTemplateService weChatAppTemplateService, GlobalPlatformApi globalPlatformApi, WxRedisService wxRedisService, RestTemplate restTemplate, WxBasicApi wxBasicApi, WxOpenBasicApi wxOpenBasicApi, WeChatAppInfoService weChatAppInfoService) {
        this.accessTokenService = accessTokenService;
        this.wxMessageApi = wxMessageApi;
        this.weChatAppTemplateService = weChatAppTemplateService;
        this.globalPlatformApi = globalPlatformApi;
        this.wxRedisService = wxRedisService;
        this.restTemplate = restTemplate;
        this.wxBasicApi = wxBasicApi;
        this.wxOpenBasicApi = wxOpenBasicApi;
        this.weChatAppInfoService = weChatAppInfoService;
    }

    /**
     * 发送模板消息
     *
     * @param in
     * @return
     */
    public void send(final TemplateMsgSendIn in) {
        TemplateMsgResponse response = null;
        TemplateMsgRequest request = in.getTemplateBody();
        String authorizerAccessToken = accessTokenService.getAuthorizerAccessToken(in.getAppId());
        if (StringUtils.isEmpty(request.getTemplateId())) {
            //根据消息类型和appId获取templateId
            String templateId = weChatAppTemplateService.findByAppIdAndTemplateBaseType(in.getAppId(), in.getTemplateBaseType());
            request.setTemplateId(templateId);
        }
        for (String openId : in.getOpenIds()) {
            request.setToUser(openId);
            response = wxMessageApi.sendTemplateMessage(request, authorizerAccessToken);
            if (response.getErrCode() == 0) {
                log.info("消息发送成功. openId[{}],templateId:[{}],response : [{}]", request.getToUser(), request.getTemplateId(), response);
            } else {
                log.error("消息发送失败. openId[{}],templateId:[{}],response : [{}]", request.getToUser(), request.getTemplateId(), response);
            }
        }
    }

    /**
     * 根据appId获取租户的域名(缓存)
     *
     * @param appId
     * @return
     */
    public TenantInternalConfigDto getHostByAppId(final String appId) {
        log.info("getHostByAppId appId [{}]",appId);
        //先从缓存中获取
        TenantInternalConfigDto tenantConfig = wxRedisService.getTenantHostByAppId(appId);
        if (tenantConfig != null) {
            return tenantConfig;
        }
        //根据appId获取app名称和租户Id
        AppConfigIn appConfigIn = new AppConfigIn().setAppId(appId);
        appConfigIn.setPagesize(1);
        Api<List<AppConfigOut>> appConfigResult = globalPlatformApi.getOrgAppConfigs(appConfigIn);
        log.info("getOrgAppConfigs request [{}] response [{}]",appConfigIn, JSONObject.toJSONString(appConfigResult));
        if (CollectionUtils.isEmpty(appConfigResult.getData())) {
            throw BEUtil.failNormal("未查询到appId配置信息");
        }
        AppConfigOut appConfigOut = appConfigResult.getData().get(0);
        return getConfigByTenantId(appConfigOut.getTenantId(), appId);
    }

    /**
     * 根据租户Id获取租户配置信息
     *
     * @param tenantId 租户Id
     * @param appId    公众号或小程序的appId , 或者是商城访问时租户Id
     * @return
     */
    public TenantInternalConfigDto getConfigByTenantId(Integer tenantId, final String appId) {
        Api<TenantInternalConfigDto> tenantConfigResult = globalPlatformApi.getTenantsWithConfigs(tenantId);
        log.info("根据租户Id查询租户信息 request : [{}] response : [{}]", tenantId, tenantConfigResult);
        if (tenantConfigResult.isSuccess() && tenantConfigResult.getData() != null) {
            TenantInternalConfigDto tenantConfig = tenantConfigResult.getData();
            String host = null;
            if (!CollectionUtils.isEmpty(tenantConfigResult.getData().getEnvInfos())) {
                for (TenantInternalConfigDto.EnvInfo envInfo : tenantConfigResult.getData().getEnvInfos()) {
                    if ("titan".equalsIgnoreCase(envInfo.getAppType())) {
                        host = envInfo.getEnvId();
                        break;
                    }
                }
            }
            if (StringUtils.isNotEmpty(host)) {
                if ((!appId.equals("" + tenantId)) && (!host.startsWith("http"))) {
                    host = "http://" + host;
                }
                tenantConfig.setTitanHost(host);
                //缓存
                wxRedisService.setAndExpireTenantHostByAppId(appId, tenantConfig);
                return tenantConfig;
            }
        }
        throw BEUtil.failNormal("未查询到集团域名配置信息");
    }

    /**
     * 发送事件通知给互联网
     *
     * @param request
     */
    public void sendEventMsgToTitan(PublicEventMsgRequest request) {
        request.validate();
        //获取appId对应租户配置信息
        TenantInternalConfigDto tenantConfig = getHostByAppId(request.getAppId());
        request.setTenantId(tenantConfig.getTenantId());
        String url = tenantConfig.findHttpFullGroupDomainName() + WeChatConstant.PUBLIC_EVENT_URL_PATH;
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add(HeaderConstant.HEADER_TENANT_ID, request.getTenantId() + "");
        requestHeaders.add("Content-type", "application/json;charset=UTF-8");
        HttpEntity<PublicEventMsgRequest> httpEntity = new HttpEntity<>(request, requestHeaders);
        ResultVO resultVO = restTemplate.postForObject(url, httpEntity, ResultVO.class);
        log.info("sendEventMsgToTitan request : [{}] response : [{}]", request, resultVO);
    }

    /**
     * 生成公众号的带参二维码
     *
     * @param qrCodeCreateRequest
     * @param publicAppId         公众号appId
     * @return
     */
    public QrCodeCreateResponse qrCodeCreate(QrCodeCreateRequest qrCodeCreateRequest, String publicAppId) {
        QrCodeCreateResponse response = wxBasicApi.qrCodeCreate(qrCodeCreateRequest, accessTokenService.getAuthorizerAccessToken(publicAppId));
        if (!response.successful()) {
            log.error("生成公众号的带参二维码异常 request : [{}] publicAppId : [{}] response : [{}]", qrCodeCreateRequest, publicAppId, response);
        }
        return response;
    }

    public OpenOauth2AccessTokenResponse getOpenIdByCodeAndAppId(String appId, String code) {
        String openAccessToken = accessTokenService.getOpenAccessToken();
        OpenOauth2AccessTokenResponse response = wxOpenBasicApi.oauth2AccessToken(appId, code, openAccessToken);
        response.checkSuccess();
        return response;
    }

    /**
     * 通过 code和appId 换取 用户基本信息
     *
     * @param appId
     * @param code
     * @return
     */
    public WxUserInfo getUserInfoByCodeAndAppId(String appId, String code) {
        OpenOauth2AccessTokenResponse accessTokenResponse = getOpenIdByCodeAndAppId(appId, code);
        //根据accessToken获取用户基本信息
        WxUserInfo userInfo = wxBasicApi.getOauth2UserInfo(accessTokenResponse.getOpenId(), accessTokenResponse.getAccessToken());
        userInfo.checkSuccess();
        return userInfo;
    }

    /**
     * 获取微信网页js.config
     *
     * @param url   前端页面url
     * @param appId 公众号appId
     */
    public JsConfigResp getJsConfig(String url, String appId) {
        long timestamp = System.currentTimeMillis() / 1000;
        String nonceStr = RandomStringUtils.randomAlphabetic(16);
        JsConfigResp resp = new JsConfigResp();
        resp.setAppId(appId).setTimestamp(timestamp).setNonceStr(nonceStr);
        resp.setSignature(DigestUtils.sha1Hex("jsapi_ticket=" + accessTokenService.getAuthorizerTicket(appId, WxApiTicket.TicketType.JS_API) + "&noncestr=" + nonceStr + "&timestamp=" + timestamp + "&url=" + url));
        return resp;
    }


    /**
     * 根据tenantId获取租户信息(缓存）
     *
     * @param tenantId
     * @return
     */
    public TenantInternalConfigDto getTenantConfigByTenantId(Integer tenantId) {
        //先从缓存中获取
        TenantInternalConfigDto tenantConfig = wxRedisService.getTenantHostByAppId("" + tenantId);
        if (tenantConfig != null) {
            return tenantConfig;
        }
        return getConfigByTenantId(tenantId, "" + tenantId);
    }

    public PublicLoginInfoOut domainAndAppId(Integer tenantId) {
        String titanHost = getTenantConfigByTenantId(tenantId).getTitanHost();
        PublicLoginInfoOut out = new PublicLoginInfoOut();
        out.setTitanDomain(titanHost);
        //获取租户appId
        AppInfoByOrgAndTypeOut info = weChatAppInfoService.getAppIdByOrgIdAndType(tenantId, null, WeChatEnum.AppType.PUBLIC.getCode());
        out.setComponentAppId(info.getComponentAppId()).setAppId(info.getAppId());
        return out;
    }
}