package com.thc.platform.modules.wechat.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.dto.AppConfigIn;
import com.thc.platform.external.dto.AppConfigOut;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.wechat.config.ApiConfig;
import com.thc.platform.modules.wechat.constant.WeChatConstant;
import com.thc.platform.modules.wechat.constant.WeChatEnum;
import com.thc.platform.modules.wechat.dao.WeChatAppInfoDao;
import com.thc.platform.modules.wechat.dto.AppInfoByOrgAndTypeOut;
import com.thc.platform.modules.wechat.dto.WeChatAppInfoOut;
import com.thc.platform.modules.wechat.entity.WeChatAppInfoEntity;
import com.titan.wechat.common.api.business.dto.AppDetailInfoResp;
import com.titan.wechat.common.api.business.dto.CreateUnAuthAppIdInfoIn;
import com.titan.wechat.common.api.open.WxOpenBasicApi;
import com.titan.wechat.common.api.open.response.AuthorizationInfo;
import com.titan.wechat.common.api.open.response.OpenAuthorizerInfoResponse;
import com.titan.wechat.common.api.open.response.OpenPreAuthCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description 授权管理service
 * @Author ZWen
 * @Date 2019/12/2 11:42 AM
 * @Version 1.0
 **/
@Slf4j
@Service
public class WeChatAppInfoService extends ServiceImpl<WeChatAppInfoDao, WeChatAppInfoEntity> {

    @Value("#{'${envInfo.envDomain}' + '/api/' + '${spring.application.name}'}")
    private String baseUrl;

    private final GlobalPlatformService globalPlatformService;
    private final AccessTokenService accessTokenService;
    private final WxOpenBasicApi wxOpenBasicApi;
    private final ApiConfig.ComponentProperties componentProperties;
    private final WeChatOpenService weChatOpenService;

    public WeChatAppInfoService(GlobalPlatformService globalPlatformService, AccessTokenService accessTokenService, WxOpenBasicApi wxOpenBasicApi, ApiConfig.ComponentProperties componentProperties, WeChatOpenService weChatOpenService) {
        this.globalPlatformService = globalPlatformService;
        this.accessTokenService = accessTokenService;
        this.wxOpenBasicApi = wxOpenBasicApi;
        this.componentProperties = componentProperties;
        this.weChatOpenService = weChatOpenService;
    }

    /**
     * 保存授权公众号信息
     *
     * @param info 授权或更新授权通知
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void saveByAuth(AuthorizationInfo info) {
        AppConfigOut appConfigOut = null;
        WeChatAppInfoEntity entity = null;
        //根据appId获取app名称和租户Id
        AppConfigIn appConfigIn = new AppConfigIn().setAppId(info.getAppId());
        appConfigIn.setPagesize(1);
        List<AppConfigOut> list = globalPlatformService.getTenantMiniAppConfig(appConfigIn);
        //兼容商城授权，AppConfigOut没有,则去WeChatAppInfoEntity查询
        if (!CollectionUtils.isEmpty(list)) {
            appConfigOut = list.get(0);
        }
        if (appConfigOut != null) {
            entity = dealWithAppConfig(info, appConfigOut);
        } else {
            entity = dealWithMall(info);
        }
        //将appId绑定在开放平台下,获取openAppId
        String openAppId = weChatOpenService.bindAppIdToOpen(entity.getTenantId(), info.getAppId());
        //更新信息
        entity.setModifyTime(new Date())
                .setStatus(WeChatEnum.AppAuthStatus.YES.getCode())
                .setFuncInfo(info.getFuncInfoList().stream().map(e -> e.getCategory().getId()).collect(Collectors.joining(",")));
        entity.setOpenAppId(openAppId);
        if (StringUtils.isEmpty(entity.getId())) {
            entity.setId(ShortUUID.uuid());
            baseMapper.insert(entity);
        } else {
            baseMapper.updateById(entity);
        }
    }

    private WeChatAppInfoEntity dealWithAppConfig(AuthorizationInfo info, AppConfigOut appConfigOut) {
        WeChatAppInfoEntity entity = baseMapper.selectOne(Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getAppId, info.getAppId()));
        if (entity == null) {
            //创建信息
            entity = new WeChatAppInfoEntity();
            entity.setCreateTime(new Date())
                    .setTenantId(appConfigOut.getTenantId())
                    .setAppId(info.getAppId())
                    .setAppName(appConfigOut.getAppName())
                    .setAppType(appConfigOut.getTypeCode());
        }
        return entity;
    }

    /**
     * 商城授权
     *
     * @param info
     * @return
     */
    private WeChatAppInfoEntity dealWithMall(AuthorizationInfo info) {
        List<WeChatAppInfoEntity> entityList = baseMapper.selectList(Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getAppId, info.getAppId()));
        if (CollectionUtils.isEmpty(entityList)) {
            throw BEUtil.failNormal("未查询到商城appId配置信息");
        }
        WeChatAppInfoEntity entity = entityList.get(0);
        //获取app信息
        OpenAuthorizerInfoResponse authorizerInfo = wxOpenBasicApi.authorizerInfo(accessTokenService.getOpenAccessToken(), info.getAppId());
        authorizerInfo.checkSuccess();
        entity.setAppName(authorizerInfo.getAuthorizerInfo().getNickName());
        return entity;
    }


    /**
     * 取消授权
     *
     * @param appId
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void unAuth(final String appId) {
        List<WeChatAppInfoEntity> list = baseMapper.selectList(Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getAppId, appId));
        if (!CollectionUtils.isEmpty(list)) {
            for (WeChatAppInfoEntity entity : list) {
                entity.setStatus(WeChatEnum.AppAuthStatus.CANCEL.getCode())
                        .setModifyTime(new Date());
                baseMapper.updateById(entity);
            }
        }
    }

    /**
     * 根据appId列表查询appId授权信息
     *
     * @param tenantId 租户Id
     * @return key=appId,value=appInfoOut
     */
    public List<WeChatAppInfoOut> listByTenant(final String tenantId) {
        //获取租户的公众号和小程序列表信息，去重
        AppConfigIn appConfigIn = new AppConfigIn().setTenantId(tenantId);
        appConfigIn.setPagesize(100);
        List<AppConfigOut> appConfigOutList = globalPlatformService.getTenantMiniAppConfig(appConfigIn);
        if (CollectionUtils.isEmpty(appConfigOutList)) {
            return Collections.EMPTY_LIST;
        }
        Set<String> appIds = appConfigOutList.stream().map(AppConfigOut::getAppId).collect(Collectors.toSet());
        if (CollectionUtils.isEmpty(appIds)) {
            throw new IllegalArgumentException("appIds不能为空");
        }
        List<WeChatAppInfoEntity> appInfoList = baseMapper.selectList(Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getTenantId, tenantId));
        List<WeChatAppInfoOut> resultList = new ArrayList<>(appIds.size());
        for (String appId : appIds) {
            WeChatAppInfoOut out = new WeChatAppInfoOut();
            out.setAppId(appId);
            for (AppConfigOut appConfigOut : appConfigOutList) {
                if (appId.equals(appConfigOut.getAppId())) {
                    //设置appName和typeCode
                    out.setAppName(appConfigOut.getAppName());
                    out.setTypeCode(appConfigOut.getTypeCode());
                    break;
                }
            }
            for (WeChatAppInfoEntity appInfo : appInfoList) {
                if (appId.equals(appInfo.getAppId())) {
                    out.setStatus(appInfo.getStatus());
                    out.setFuncInfo(appInfo.getFuncInfo());
                    break;
                }
            }
            resultList.add(out);
        }
        //修改为兼容模式，可以在AppConfigOut没有，直接在WeChatAppInfoEntity设置(商城来源)
        List<WeChatAppInfoOut> additionList = new ArrayList<>();
        for (WeChatAppInfoEntity entity : appInfoList) {
            boolean include = false;
            for (WeChatAppInfoOut out : resultList) {
                if (out.getAppId().equals(entity.getAppId())) {
                    include = true;
                    break;
                }
            }
            if (!include) {
                WeChatAppInfoOut out = new WeChatAppInfoOut();
                out.setAppId(entity.getAppId());
                out.setAppName(entity.getAppName());
                out.setTypeCode(entity.getAppType());
                out.setStatus(entity.getStatus());
                out.setFuncInfo(entity.getFuncInfo());
                additionList.add(out);
            }
        }
        resultList.addAll(additionList);
        return resultList;
    }

    /**
     * 获取公众号代开发管理员授权地址
     *
     * @param appId
     * @param typeCode 1微信公众号;2小程序
     * @return
     */
    public String getAuthUrl(final String appId, Integer typeCode) {
        String openAccessToken = accessTokenService.getOpenAccessToken();
        OpenPreAuthCodeResponse preAuthCode = wxOpenBasicApi.getPreAuthCode(openAccessToken);
        log.info("预授权码 is:{}", preAuthCode);
        String redirectUrl = baseUrl + WeChatConstant.PATH_PREFIX + "/open/authSuccess";
        String result = null;
        try {
            result = MessageFormat.format(WeChatConstant.AUTH_URL, typeCode, componentProperties.getAppId(), preAuthCode.getPreAuthCode(), URLEncoder.encode(redirectUrl, "UTF-8"), appId);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        log.info("代开发管理员授权地址 is : {}", result);
        return result;
    }

    public void checkIsAuth(String appId, Integer typeCode, Integer tenantId) {
        String openAccessToken = accessTokenService.getOpenAccessToken();
        OpenAuthorizerInfoResponse response = wxOpenBasicApi.authorizerInfo(openAccessToken, appId);
        if (response != null && response.successful()) {
            OpenAuthorizerInfoResponse.AuthorizationInfo info = response.getAuthorizationInfo();

            WeChatAppInfoEntity entity = baseMapper.selectOne(
                    Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getAppId, appId).eq(WeChatAppInfoEntity::getTenantId, tenantId));
            if (entity != null) {
                this.baseMapper.deleteById(entity.getId());
            }
            //插入新绑定信息
            entity = new WeChatAppInfoEntity();
            entity.setId(ShortUUID.uuid());
            entity.setTenantId(tenantId);
            entity.setAppId(appId);
            entity.setAppType(typeCode);
            entity.setAppName(response.getAuthorizerInfo().getNickName());
            entity.setCreateTime(new Date()).setModifyTime(new Date())
                    .setStatus(WeChatEnum.AppAuthStatus.YES.getCode())
                    .setFuncInfo(info.getFuncInfoList().stream().map(e -> e.getCategory().getId()).collect(Collectors.joining(",")));
            //将appId绑定在开放平台下,获取openAppId
            String openAppId = weChatOpenService.bindAppIdToOpen(tenantId, appId);
            entity.setOpenAppId(openAppId);

            baseMapper.insert(entity);


            throw BEUtil.failNormal("该APPID已授权第三方平台，已自动授权成功！");
        }

    }

    /**
     * 判断租户是否有已授权的公众号
     *
     * @param tenantId
     */
    public void checkPublicApp(Integer tenantId) {
        //首先获取是否有公众号appId配置
        AppConfigIn appConfigIn = new AppConfigIn().setTenantId("" + tenantId).setTypeCode(WeChatEnum.AppType.PUBLIC.getCode());
        appConfigIn.setPagesize(100);
        List<AppConfigOut> appConfigOutList = globalPlatformService.getTenantMiniAppConfig(appConfigIn);
        if (CollectionUtils.isEmpty(appConfigOutList)) {
            throw BEUtil.failNormal("该租户未配置微信公众号信息，请前往 租户管理-》租户信息-》租户详情-》机构信息 下配置");
        }
        Set<String> appIds = appConfigOutList.stream().map(AppConfigOut::getAppId).collect(Collectors.toSet());
        List<WeChatAppInfoEntity> list = baseMapper.selectList(Wrappers.<WeChatAppInfoEntity>lambdaQuery().in(WeChatAppInfoEntity::getAppId, appIds).eq(WeChatAppInfoEntity::getTenantId, tenantId));
        if (CollectionUtils.isEmpty(list)) {
            throw BEUtil.failNormal("该租户微信公众号未授权，请前往 租户管理-》租户信息-》租户详情-》微信配置 下申请授权");
        }

        for (String appId : appIds) {
            boolean hasAppId = false;
            for (WeChatAppInfoEntity entity : list) {
                if (appId.equals(entity.getAppId())) {
                    hasAppId = true;
                    if (entity.getStatus() == WeChatEnum.AppAuthStatus.NO.getCode()) {
                        throw BEUtil.failNormal("该租户微信公众号未授权，请前往 租户管理-》租户信息-》租户详情-》微信配置 下申请授权.appId : " + appId);
                    }
                    if (entity.getStatus() == WeChatEnum.AppAuthStatus.CANCEL.getCode()) {
                        throw BEUtil.failNormal("该租户微信公众号已取消授权，请前往 租户管理-》租户信息-》租户详情-》微信配置 下申请授权.appId : " + appId);
                    }
                }
            }
            if (!hasAppId) {
                throw BEUtil.failNormal("该租户微信公众号未授权，请前往 租户管理-》租户信息-》租户详情-》微信配置 下申请授权.appId : " + appId);
            }
        }
    }

    /**
     * 获取机构下小程序或公众号appId及第三方平台appId
     *
     * @param tenantId
     * @param orgId
     * @param appType
     * @return
     */
    public AppInfoByOrgAndTypeOut getAppIdByOrgIdAndType(Integer tenantId, String orgId, Integer appType) {
        AppConfigIn appConfigIn = new AppConfigIn().setTenantId("" + tenantId).setTypeCode(appType);
        appConfigIn.setPagesize(100);
        List<AppConfigOut> appConfigOutList = globalPlatformService.getTenantMiniAppConfig(appConfigIn);
        if (CollectionUtils.isEmpty(appConfigOutList)) {
            //直接获取授权列表
            List<WeChatAppInfoEntity> list = baseMapper.selectList(Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getStatus, WeChatEnum.AppAuthStatus.YES.getCode()).eq(WeChatAppInfoEntity::getAppType, appType).eq(WeChatAppInfoEntity::getTenantId, tenantId));
            if (CollectionUtils.isEmpty(list)) {
                throw BEUtil.failNormal("该租户未配置微信相关信息，请前往 租户管理-》租户信息-》租户详情-》机构信息 下配置");
            }
            AppInfoByOrgAndTypeOut out = new AppInfoByOrgAndTypeOut();
            out.setAppId(list.get(0).getAppId()).setComponentAppId(componentProperties.getAppId());
            return out;
        }
        String tenantAppId = null;
        String orgAppId = null;
        //首先获取机构下appId，没有的话，再获取租户下的appId
        if (StringUtils.isEmpty(orgId)) {
            orgId = String.valueOf(tenantId);
        }
        for (AppConfigOut appConfigOut : appConfigOutList) {
            if (orgId.equals(appConfigOut.getOrgId())) {
                orgAppId = appConfigOut.getAppId();
                break;
            } else if (String.valueOf(appConfigOut.getTenantId()).equals(appConfigOut.getOrgId())) {
                tenantAppId = appConfigOut.getAppId();
            }
        }
        if (StringUtils.isEmpty(orgAppId)) {
            orgAppId = tenantAppId;
        }
        AppInfoByOrgAndTypeOut out = new AppInfoByOrgAndTypeOut();
        out.setAppId(orgAppId).setComponentAppId(componentProperties.getAppId());
        return out;
    }

    /**
     * 创建租户待授权微信公众号配置信息
     *
     * @param in
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public void createUnAuthAppIdInfo(CreateUnAuthAppIdInfoIn in) {
        Date currentTime = new Date();
        WeChatAppInfoEntity entity = null;
        //判断是否存在此公众号
        List<WeChatAppInfoEntity> list = baseMapper.selectList(Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getAppId, in.getAppId()));
        if (!CollectionUtils.isEmpty(list)) {
            entity = list.get(0);
            if (entity.getStatus() == WeChatEnum.AppAuthStatus.YES.getCode()) {
                throw BEUtil.failNormal("该appId已经授权，请直接选择使用已授权公众号");
            }
            if (entity.getAppType() == WeChatEnum.AppType.MINI.getCode()) {
                throw BEUtil.failNormal("该appId为小程序，请填写正确的公众号appId");
            }
            if (!in.getTenantId().equals(entity.getTenantId())) {
                throw BEUtil.failNormal("该appId已被其他租户绑定");
            }
        }
        if (entity == null) {
            //创建信息
            entity = new WeChatAppInfoEntity();
            entity.setCreateTime(currentTime)
                    .setTenantId(in.getTenantId())
                    .setAppId(in.getAppId())
                    .setAppName("未知(待绑定后更新)")
                    .setAppType(WeChatEnum.AppType.PUBLIC.getCode())
                    .setStatus(WeChatEnum.AppAuthStatus.NO.getCode());
        }
        entity.setModifyTime(currentTime);
        if (StringUtils.isEmpty(entity.getId())) {
            entity.setId(ShortUUID.uuid());
            baseMapper.insert(entity);
        } else {
            baseMapper.updateById(entity);
        }
    }

    /**
     * 获取授权方appId对应的账号信息和授权信息
     *
     * @param appId
     * @param tenantId
     * @return
     */
    public AppDetailInfoResp findDetailByAppId(String appId, Integer tenantId) {
        //判断该租户是否存在此公众号
        List<WeChatAppInfoEntity> list = baseMapper.selectList(Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getAppId, appId).eq(WeChatAppInfoEntity::getTenantId, tenantId));
        if (CollectionUtils.isEmpty(list)) {
            throw BEUtil.failNormal("未查询到 appId is " + appId + "; tenantId is " + tenantId);
        }
        WeChatAppInfoEntity entity = list.get(0);
        AppDetailInfoResp resp = new AppDetailInfoResp();
        resp.setAppId(appId).setTenantId(tenantId).setStatus(entity.getStatus()).setNickName(entity.getAppName());
        //如果已授权，获取app信息
        if (entity.getStatus() == WeChatEnum.AppAuthStatus.YES.getCode()) {
            OpenAuthorizerInfoResponse authorizerInfo = wxOpenBasicApi.authorizerInfo(accessTokenService.getOpenAccessToken(), appId);
            authorizerInfo.checkSuccess();
            resp.setAuthorizerInfoResponse(authorizerInfo);
        }
        return resp;
    }

    /**
     * 获取租户已授权公众号列表
     *
     * @param tenantId
     * @return
     */
    public List<AppDetailInfoResp> listAuthPubAppByTenantId(Integer tenantId) {
        List<WeChatAppInfoEntity> list = baseMapper.selectList(Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getStatus, WeChatEnum.AppAuthStatus.YES.getCode()).eq(WeChatAppInfoEntity::getAppType, WeChatEnum.AppType.PUBLIC.getCode()).eq(WeChatAppInfoEntity::getTenantId, tenantId));
        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_LIST;
        }
        List<AppDetailInfoResp> resultList = new ArrayList<>();
        for (WeChatAppInfoEntity entity : list) {
            AppDetailInfoResp resp = new AppDetailInfoResp();
            resp.setAppId(entity.getAppId()).setTenantId(tenantId).setStatus(entity.getStatus()).setNickName(entity.getAppName());
            //获取app信息
            OpenAuthorizerInfoResponse authorizerInfo = wxOpenBasicApi.authorizerInfo(accessTokenService.getOpenAccessToken(), entity.getAppId());
            authorizerInfo.checkSuccess();
            resp.setAuthorizerInfoResponse(authorizerInfo);
            resultList.add(resp);
        }
        return resultList;
    }
}