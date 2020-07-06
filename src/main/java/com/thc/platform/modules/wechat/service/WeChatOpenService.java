package com.thc.platform.modules.wechat.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.modules.wechat.dao.WeChatOpenDao;
import com.thc.platform.modules.wechat.entity.WeChatOpenEntity;
import com.titan.wechat.common.api.open.WxOpenBasicApi;
import com.titan.wechat.common.api.open.response.CreateOpenAndBindResponse;
import com.titan.wechat.common.api.support.WxResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/12/18 5:59 PM
 * @Version 1.0
 **/
@Slf4j
@Service
public class WeChatOpenService extends ServiceImpl<WeChatOpenDao, WeChatOpenEntity> {

    private final AccessTokenService accessTokenService;
    private final WxOpenBasicApi wxOpenBasicApi;

    public WeChatOpenService(AccessTokenService accessTokenService, WxOpenBasicApi wxOpenBasicApi) {
        this.accessTokenService = accessTokenService;
        this.wxOpenBasicApi = wxOpenBasicApi;
    }

    /**
     * 将appId绑定在开放平台下
     *
     * @param tenantId
     * @param appId
     * @return
     */
    public String bindAppIdToOpen(Integer tenantId, String appId) {
        try {
            //获取租户的openAppId
            WeChatOpenEntity weChatOpenEntity = baseMapper.selectOne(Wrappers.<WeChatOpenEntity>lambdaQuery().eq(WeChatOpenEntity::getTenantId, tenantId));
            String accessToken = accessTokenService.getAuthorizerAccessToken(appId);
            String openAppId = null;
            boolean needBind = false;
            if (weChatOpenEntity == null) {
                //判断appId是否已经绑定
                CreateOpenAndBindResponse createOpenAndBindResponse = wxOpenBasicApi.getOpenByAppId(appId, accessToken);
                if (createOpenAndBindResponse.successful()) {
                    openAppId = createOpenAndBindResponse.getOpenAppId();
                } else {
                    //未绑定，创建绑定
                    createOpenAndBindResponse = wxOpenBasicApi.createOpenAndBind(appId, accessToken);
                    if (createOpenAndBindResponse.successful()) {
                        log.info("创建开放平台帐号并绑定公众号/小程序成功，appId : [{}] response : [{}]", appId, createOpenAndBindResponse);
                        openAppId = createOpenAndBindResponse.getOpenAppId();
                    } else {
                        log.error("创建开放平台帐号并绑定公众号/小程序失败，appId : [{}] response : [{}]", appId, createOpenAndBindResponse);
                    }
                }
            } else {
                openAppId = weChatOpenEntity.getOpenAppId();
                //判断appId是否已经绑定
                CreateOpenAndBindResponse createOpenAndBindResponse = wxOpenBasicApi.getOpenByAppId(appId, accessToken);
                if (createOpenAndBindResponse.successful()) {
                    log.info("获取公众号/小程序所绑定的开放平台帐号成功,存在已绑定openAppId，appId : [{}] response : [{}]", appId, createOpenAndBindResponse);
                    if (!createOpenAndBindResponse.getOpenAppId().equals(openAppId)) {
                        needBind = true;
                        //如果和数据库中的openAppId不一致，解除绑定
                        WxResponse unBindResponse = wxOpenBasicApi.unBindAppIdToOpen(appId, createOpenAndBindResponse.getOpenAppId(), accessToken);
                        if (unBindResponse.successful()) {
                            log.info("将公众号/小程序从开放平台帐号下解绑成功，appId : [{}] response : [{}]", appId, unBindResponse);
                        } else {
                            log.error("将公众号/小程序从开放平台帐号下解绑失败，appId : [{}] response : [{}]", appId, unBindResponse);
                        }
                    }
                } else {
                    needBind = true;
                    log.info("获取公众号/小程序所绑定的开放平台帐号成功,不存在已绑定openAppId，appId : [{}] response : [{}]", appId, createOpenAndBindResponse);
                }
                //绑定
                if (needBind) {
                    WxResponse bindResponse = wxOpenBasicApi.bindAppIdToOpen(appId, openAppId, accessToken);
                    if (!bindResponse.successful()) {
                        log.error("将公众号/小程序绑定到开放平台帐号下失败，appId : [{}] response : [{}]", appId, bindResponse);
                    } else {
                        log.info("将公众号/小程序绑定到开放平台帐号下成功，appId : [{}] response : [{}]", appId, bindResponse);
                    }
                }
            }
            //如果数据库无记录，且创建openAppId成功，存入数据库
            if (weChatOpenEntity == null && StringUtils.isNotEmpty(openAppId)) {
                weChatOpenEntity = new WeChatOpenEntity();
                weChatOpenEntity.setId(ShortUUID.uuid());
                weChatOpenEntity.setTenantId(tenantId).setOpenAppId(openAppId);
                Date date = new Date();
                weChatOpenEntity.setModifyTime(date).setCreateTime(date);
                baseMapper.insert(weChatOpenEntity);
            }
            return openAppId;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}