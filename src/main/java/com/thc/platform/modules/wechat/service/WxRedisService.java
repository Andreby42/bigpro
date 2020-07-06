package com.thc.platform.modules.wechat.service;

import com.alibaba.fastjson.JSON;
import com.thc.platform.external.dto.TenantInternalConfigDto;
import com.thc.platform.modules.wechat.constant.WeChatConstant;
import com.titan.wechat.common.api.basic.response.WxApiTicket;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description redis操作
 * @Author ZWen
 * @Date 2018/12/11 1:51 PM
 * @Version 1.0
 **/
@Slf4j
@Component
public class WxRedisService {

    private final RedisTemplate<String, String> redisTemplate;

    public WxRedisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    public Set<String> keys(String keyPattern) {
        if (StringUtils.isEmpty(keyPattern)) {
            return Collections.emptySet();
        }
        return redisTemplate.keys(keyPattern);
    }


    public String get(String key) {
        if (StringUtils.isEmpty(key)) {
            return null;
        }
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 通过appId获取租户域名
     *
     * @param appId
     * @return host
     */
    public TenantInternalConfigDto getTenantHostByAppId(String appId) {
        String key = WeChatConstant.TENANT_HOST_BY_APPID_PREFIX + appId;
        String value = redisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(value)) {
            return null;
        }
        return JSON.parseObject(value, TenantInternalConfigDto.class);
    }


    /**
     * 缓存通过appId获取的租户域名 2小时
     *
     * @param appId        appId
     * @param tenantConfig 集团全域名
     * @return
     */
    public void setAndExpireTenantHostByAppId(String appId, TenantInternalConfigDto tenantConfig) {
        String key = WeChatConstant.TENANT_HOST_BY_APPID_PREFIX + appId;
        redisTemplate.opsForValue().set(key, JSON.toJSONString(tenantConfig), WeChatConstant.TENANT_HOST_BY_APPID_EXPIRE_HOURS, TimeUnit.HOURS);
    }

    /**
     * 是否可以发送模板消息总开关 true为可发送
     *
     * @return
     */
    public boolean canSendTemplateMsg() {
        String flag = redisTemplate.opsForValue().get(WeChatConstant.TEMPLATE_MSG_SEND_FLAG);
        return WeChatConstant.TEMPLATE_MSG_CAN_SEND.equalsIgnoreCase(flag);
    }

    /**
     * 设置发送模板消息总开关
     *
     * @return
     */
    public void setTemplateMsgSendFlag(String value) {
        set(WeChatConstant.TEMPLATE_MSG_SEND_FLAG, value);
    }

    /**
     * 获取开放平台-第三方平台的ticket
     *
     * @return
     */
    public String getOpenVerifyTicket() {
        return redisTemplate.opsForValue().get(WeChatConstant.WE_CHAT_COMPONENT_VERIFY_TICKET_KEY);
    }

    /**
     * 设置第三方平台的ticket
     *
     * @return
     */
    public void setAndExpireOpenVerifyTicket(String value) {
        redisTemplate.opsForValue().set(WeChatConstant.WE_CHAT_COMPONENT_VERIFY_TICKET_KEY, value, 2, TimeUnit.HOURS);
    }

    /**
     * 获取开放平台-第三方平台的accessToken
     *
     * @return
     */
    public String getOpenAccessToken() {
        return redisTemplate.opsForValue().get(WeChatConstant.WE_CHAT_COMPONENT_ACCESS_TOKEN_KEY);
    }

    /**
     * 设置第三方平台的accessToken,过期时间为60分钟
     *
     * @return
     */
    public void setAndExpireOpenAccessToken(String value) {
        redisTemplate.opsForValue().set(WeChatConstant.WE_CHAT_COMPONENT_ACCESS_TOKEN_KEY, value, 60, TimeUnit.MINUTES);
    }

    /**
     * 第三方平台管理的公众号的authorizer_accessToken
     *
     * @return
     */
    public String getAuthorizerAccessToken(String appId) {
        return get(WeChatConstant.AUTHORIZER_ACCESS_TOKEN_REDIS_KEY + appId);
    }

    /**
     * 第三方平台管理的公众号的authorizer_accessToken,过期时间为110分钟
     *
     * @return
     */
    public void setAndExpireAuthorizerAccessToken(String value, String appId) {
        redisTemplate.opsForValue().set(WeChatConstant.AUTHORIZER_ACCESS_TOKEN_REDIS_KEY + appId, value, 110, TimeUnit.MINUTES);
    }

    /**
     * 第三方平台管理的appId的某类型的ticket
     *
     * @return
     */
    public String getAuthorizerTicket(String appId, WxApiTicket.TicketType ticketType) {
        return get(WeChatConstant.AUTHORIZER_TICKET_REDIS_KEY + appId + ":" + ticketType.code);
    }

    /**
     * 第三方平台管理的appId的某类型的ticket,过期时间为110分钟
     *
     * @return
     */
    public void setAndExpireAuthorizerTicket(String value, String appId, WxApiTicket.TicketType ticketType) {
        redisTemplate.opsForValue().set(WeChatConstant.AUTHORIZER_TICKET_REDIS_KEY + appId + ":" + ticketType.code, value, 110, TimeUnit.MINUTES);
    }

    /**
     * 第三方平台管理的公众号的authorizer_refreshToken
     *
     * @return
     */
    public String getAuthorizerRefreshToken(String appId) {
        return get(WeChatConstant.AUTHORIZER_REFRESH_TOKEN_REDIS_KEY + appId);
    }


    /**
     * 第三方平台管理的公众号的authorizer_refreshToken
     *
     * @return
     */
    public void setAuthorizerRefreshToken(String value, String appId) {
        set(WeChatConstant.AUTHORIZER_REFRESH_TOKEN_REDIS_KEY + appId, value);
    }

    public void set(String key, String value) {
        if (StringUtils.isEmpty(key)
                || StringUtils.isEmpty(value)) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 删除某一前缀的所有k:v
     * 注意：删除的前缀应该是就近一级  eg:  key=test:aa:bb:12345 那么他的前缀应该是test:aa:bb:*   这样才能删除
     *
     * @param prefix
     */
    public Long delWeChatRedisByPrefix(String prefix) {
        if (StringUtils.isEmpty(prefix) || (!prefix.startsWith(WeChatConstant.WE_CHAT_REDIS_PREFIX))) {
            throw new IllegalArgumentException("prefix不能为空或前缀有误");
        }
        log.info("删除前缀为【{}】的所有的key", prefix);
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (CollectionUtils.isNotEmpty(keys)) {
            Long delCount = redisTemplate.delete(keys);
            log.info("批量删除前缀为【{}】的key【{}】个", prefix, delCount);
            return delCount;
        }
        return 0L;
    }
}