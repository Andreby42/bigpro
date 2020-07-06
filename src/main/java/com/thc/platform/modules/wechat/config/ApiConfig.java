package com.thc.platform.modules.wechat.config;

import com.titan.wechat.common.api.basic.WxBasicApi;
import com.titan.wechat.common.api.message.WxMessageApi;
import com.titan.wechat.common.api.message.WxMsgHandler;
import com.titan.wechat.common.api.message.WxMsgService;
import com.titan.wechat.common.api.mini.WxMiniApi;
import com.titan.wechat.common.api.open.OpenMsgHandler;
import com.titan.wechat.common.api.open.OpenMsgService;
import com.titan.wechat.common.api.open.WxOpenBasicApi;
import com.titan.wechat.common.api.support.WxHttpClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestTemplate;

import javax.validation.constraints.NotNull;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/11/16 5:22 PM
 * @Version 1.0
 **/
@Order(1)
@Configuration
@EnableConfigurationProperties({ApiConfig.ComponentProperties.class})
public class ApiConfig {

    private final ComponentProperties props;

    public ApiConfig(ComponentProperties props) {
        this.props = props;
    }

    /**
     * 微信http请求客户端
     *
     * @return
     */
    @Bean
    WxHttpClient wxHttpClient() {
        return new WxHttpClient();
    }

    /**
     * 调用基本接口bean
     *
     * @return
     */
    @Bean
    WxBasicApi wxBasicApi() {
        return new WxBasicApi(wxHttpClient());
    }

//    @Bean
//    WxJsApi wxJsApi() {
//        return new WxJsApi(wxHttpClient());
//    }

    /**
     * 微信开放平台基础接口
     *
     * @return
     */
    @Bean
    WxOpenBasicApi wxOpenBasicApi() {
        return new WxOpenBasicApi(new WxHttpClient(), props.getAppId(), props.getAppSecret());
    }

    /**
     * 发送消息给用户bean
     */
    @Bean
    WxMessageApi wxMessageApi() {
        return new WxMessageApi(wxHttpClient());
    }

    /**
     * 小程序api bean
     */
    @Bean
    WxMiniApi wxMiniApi() {
        return new WxMiniApi(wxHttpClient());
    }

    /**
     * restTemplate bean
     */
    @Bean
    @ConditionalOnMissingBean(RestTemplate.class)
    RestTemplate restTemplate() {
        return new RestTemplate();
    }


    /**
     * 将已经注入到spring的OpenMsgHandler实现类集合注入到OpenMsgService
     *
     * @param openMsgHandlers
     * @return
     */
    @Bean
    OpenMsgService openMsgService(OpenMsgHandler[] openMsgHandlers) {
        OpenMsgService openMsgService = new OpenMsgService();
        openMsgService.registerHandlers(openMsgHandlers);
        return openMsgService;
    }

    /**
     * 微信公众号处理Service
     *
     * @param wxMsgHandlers
     * @return
     */
    @Bean
    WxMsgService wxMsgService(WxMsgHandler[] wxMsgHandlers) {
        WxMsgService wxMsgService = new WxMsgService();
        wxMsgService.registerHandlers(wxMsgHandlers);
        return wxMsgService;
    }


    /**
     * 微信开放平台信息
     * 不同环境值可能不一样，需运维配置
     */
    @Validated
    @ConfigurationProperties(prefix = "wechat-open")
    public static class ComponentProperties {

        private String appId;

        private String appSecret;

        private String token;

        private String aesKey;

        @NotNull
        public String getAppId() {
            return appId;
        }

        public void setAppId(String appId) {
            this.appId = appId;
        }

        @NotNull
        public String getAppSecret() {
            return appSecret;
        }

        public void setAppSecret(String appSecret) {
            this.appSecret = appSecret;
        }

        @NotNull
        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        @NotNull
        public String getAesKey() {
            return aesKey;
        }

        public void setAesKey(String aesKey) {
            this.aesKey = aesKey;
        }
    }

}