package com.thc.platform.modules.wechat.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.wechat.constant.WeChatEnum;
import com.thc.platform.modules.wechat.dao.WeChatAppInfoDao;
import com.thc.platform.modules.wechat.dao.WeChatAppTemplateDao;
import com.thc.platform.modules.wechat.dto.WeChatAppTemplateOut;
import com.thc.platform.modules.wechat.dto.WeChatTemplateCreateIn;
import com.thc.platform.modules.wechat.entity.WeChatAppInfoEntity;
import com.thc.platform.modules.wechat.entity.WeChatAppTemplateEntity;
import com.titan.wechat.common.api.business.enums.TemplateBaseTypeEnum;
import com.titan.wechat.common.api.message.WxMessageApi;
import com.titan.wechat.common.api.message.send.template.AddTemplateRequest;
import com.titan.wechat.common.api.message.send.template.DelTemplateRequest;
import com.titan.wechat.common.api.message.send.template.data.AddTemplateResponse;
import com.titan.wechat.common.api.support.WxResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description 公众号模板管理service
 * @Author ZWen
 * @Date 2019/12/2 4:04 PM
 * @Version 1.0
 **/
@Slf4j
@Service
public class WeChatAppTemplateService extends ServiceImpl<WeChatAppTemplateDao, WeChatAppTemplateEntity> {

    private final WeChatAppInfoDao weChatAppInfoDao;
    private final WxMessageApi wxMessageApi;
    private final AccessTokenService accessTokenService;
    private final GlobalPlatformService globalPlatformService;

    public WeChatAppTemplateService(WeChatAppInfoDao weChatAppInfoDao, WxMessageApi wxMessageApi, AccessTokenService accessTokenService, GlobalPlatformService globalPlatformService) {
        this.weChatAppInfoDao = weChatAppInfoDao;
        this.wxMessageApi = wxMessageApi;
        this.accessTokenService = accessTokenService;
        this.globalPlatformService = globalPlatformService;
    }

    /**
     * 根据公众号appId查询模板列表(不分页)
     *
     * @param appId
     * @return
     */
    public List<WeChatAppTemplateOut> listByAppId(final String appId) {
        List<WeChatAppTemplateEntity> list = baseMapper.selectList(Wrappers.<WeChatAppTemplateEntity>lambdaQuery().eq(WeChatAppTemplateEntity::getAppId, appId).eq(WeChatAppTemplateEntity::getStatus, WeChatEnum.AppTemplateStatus.ALIVE.getCode()));
        return list.stream().map(WeChatAppTemplateOut::new).collect(Collectors.toList());
    }

    /**
     * 根据公众号appId和基础模板类型查询模板Id
     *
     * @param appId
     * @return
     */
    public String findByAppIdAndTemplateBaseType(final String appId, final TemplateBaseTypeEnum templateBaseType) {
        List<WeChatAppTemplateEntity> entityList = baseMapper.selectList(Wrappers.<WeChatAppTemplateEntity>lambdaQuery().eq(WeChatAppTemplateEntity::getAppId, appId).eq(WeChatAppTemplateEntity::getCode, templateBaseType.getCode()).eq(WeChatAppTemplateEntity::getStatus, WeChatEnum.AppTemplateStatus.ALIVE.getCode()));
        if (!CollectionUtils.isEmpty(entityList)) {
            return entityList.get(0).getTemplateId();
        }
        log.error("查询模板Id失败，appId : [{}] code : [{}]", appId, templateBaseType.getCode());
        throw BEUtil.failNormal("未查询到模板Id");
    }

    /**
     * 创建公众号模板
     *
     * @param in 创建模板入参
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public WeChatAppTemplateEntity saveTemplate(WeChatTemplateCreateIn in) {

        WeChatAppInfoEntity weChatAppInfoEntity = weChatAppInfoDao.selectOne(Wrappers.<WeChatAppInfoEntity>lambdaQuery().eq(WeChatAppInfoEntity::getAppId, in.getAppId()));
        if (weChatAppInfoEntity == null) {
            throw BEUtil.failNormal("appInfo查询失败");
        }
        if (weChatAppInfoEntity.getAppType() != WeChatEnum.AppType.PUBLIC.getCode()) {
            throw BEUtil.failNormal("app类型错误，只能是公众号");
        }
        //判断是否已经创建该类型的模板
        Integer count = baseMapper.selectCount(Wrappers.<WeChatAppTemplateEntity>lambdaQuery().eq(WeChatAppTemplateEntity::getAppId, weChatAppInfoEntity.getAppId()).eq(WeChatAppTemplateEntity::getCode, in.getTemplateBaseType().getCode()).eq(WeChatAppTemplateEntity::getStatus, WeChatEnum.AppTemplateStatus.ALIVE.getCode()));
        if (count > 0) {
            throw BEUtil.failNormal("该模板已添加，请勿重复添加");
        }
        SysUserDto sysUser = globalPlatformService.getCurrentUser();
        Date currentTime = new Date();
        //获取对应appId的accessToken
        String publicAccessToken = accessTokenService.getAuthorizerAccessToken(weChatAppInfoEntity.getAppId());
        //调用微信接口创建模板
        AddTemplateResponse addTemplateResponse = wxMessageApi.addTemplate(new AddTemplateRequest(in.getTemplateBaseType().getCode()), publicAccessToken);
        log.info("添加模板 req : [{}] resp : [{}]", in.getTemplateBaseType().getCode(), addTemplateResponse);
        addTemplateResponse.checkSuccess();
        //添加到数据库
        WeChatAppTemplateEntity entity = new WeChatAppTemplateEntity().setId(ShortUUID.uuid());
        entity.setCreatorId(sysUser.getId()).setCreatorName(sysUser.getName()).setCreateTime(currentTime);
        entity.setModifyUserId(sysUser.getId()).setModifyUserName(sysUser.getName()).setModifyTime(currentTime);
        entity.setCode(in.getTemplateBaseType().getCode())
                .setTitle(in.getTemplateBaseType().getTitle())
                .setAppId(weChatAppInfoEntity.getAppId())
                .setAppInfoId(weChatAppInfoEntity.getId())
                .setTemplateId(addTemplateResponse.getTemplateId())
                .setTenantId(weChatAppInfoEntity.getTenantId())
                .setStatus(WeChatEnum.AppTemplateStatus.ALIVE.getCode());
        baseMapper.insert(entity);
        return entity;
    }

    /**
     * 删除模板
     *
     * @param id 模板表id
     * @return 被删除模板信息
     */
    @Transactional(rollbackFor = RuntimeException.class)
    public WeChatAppTemplateEntity delTemplate(final String id) {
        WeChatAppTemplateEntity entity = baseMapper.selectById(id);
        if (entity == null) {
            throw BEUtil.failNormal("模板信息查询失败");
        }
        SysUserDto sysUser = globalPlatformService.getCurrentUser();
        Date currentTime = new Date();
        //获取accessToken
        String publicAccessToken = accessTokenService.getAuthorizerAccessToken(entity.getAppId());
        //调用接口删除模板
        WxResponse wxResponse = wxMessageApi.delTemplate(new DelTemplateRequest(entity.getTemplateId()), publicAccessToken);
        log.info("删除模板 req : [{}] resp : [{}]", entity.getTemplateId(), wxResponse);
        wxResponse.checkSuccess();
        //修改数据库信息
        entity.setModifyUserId(sysUser.getId()).setModifyUserName(sysUser.getName()).setModifyTime(currentTime);
        entity.setStatus(WeChatEnum.AppTemplateStatus.DELETE.getCode());
        baseMapper.updateById(entity);
        return entity;
    }
}