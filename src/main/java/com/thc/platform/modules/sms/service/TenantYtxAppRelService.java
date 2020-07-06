package com.thc.platform.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.modules.sms.dao.TenantYtxAppRelDao;
import com.thc.platform.modules.sms.dto.TenantYtxAppRelBatchAddIn;
import com.thc.platform.modules.sms.entity.TenantYtxAppRelEntity;

/**
 * 租户与云通讯应用关系服务
 */
@Service
public class TenantYtxAppRelService extends ServiceImpl<TenantYtxAppRelDao, TenantYtxAppRelEntity> {

	public String getAppId(Integer tenantId) {
		TenantYtxAppRelEntity entity = baseMapper.selectOne(
					Wrappers.<TenantYtxAppRelEntity>lambdaQuery()
					.eq(TenantYtxAppRelEntity::getTenantId, tenantId)
				);
		
		if(entity == null)
			throw BEUtil.failNormal("荣联短信配置错误");
		
		return entity.getAppId();
	}
	
	@Transactional
	public void add(TenantYtxAppRelBatchAddIn in) {
		String appId = in.getAppId();
		for(Integer tenantId : in.getTenantIds()) {
			if(tenantId == null)
				continue;
			
			deleteByBusiId(appId, tenantId);
			
			if(countByTenantId(tenantId) > 0)
				throw BEUtil.failNormal("一个租户只能关联一个应用信息。tenantId: " + tenantId);
			
			TenantYtxAppRelEntity entity = new TenantYtxAppRelEntity();
			entity.setId(ShortUUID.uuid());
			entity.setAppId(appId);
			entity.setTenantId(tenantId);
			save(entity);
		}
	}
	
	private void deleteByBusiId(String appId, Integer tenantId) {
		baseMapper.delete(
				Wrappers.<TenantYtxAppRelEntity>lambdaQuery()
					.eq(TenantYtxAppRelEntity::getAppId, appId)
					.eq(TenantYtxAppRelEntity::getTenantId, tenantId)
				);
	}
	
	public List<TenantYtxAppRelEntity> getByAppId(String appId) {
		return baseMapper.selectList(
					Wrappers.<TenantYtxAppRelEntity>lambdaQuery()
						.eq(TenantYtxAppRelEntity::getAppId, appId)
				);
	}
	
	public Integer countByAppId(String appId) {
		return baseMapper.selectCount(
					Wrappers.<TenantYtxAppRelEntity>lambdaQuery()
						.eq(TenantYtxAppRelEntity::getAppId, appId)
				);
	}
	
	public Integer countByTenantId(Integer tenantId) {
		return baseMapper.selectCount(
					Wrappers.<TenantYtxAppRelEntity>lambdaQuery()
						.eq(TenantYtxAppRelEntity::getTenantId, tenantId)
				);
	}
}
