package com.thc.platform.modules.sms.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.BaseEntityUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.sms.dao.SmsSendNodeTenantStatusDao;
import com.thc.platform.modules.sms.dto.ChangeTenantSendNodeStatusIn;
import com.thc.platform.modules.sms.entity.SmsSendNodeTenantStatusEntity;

@Service
public class SmsSendNodeTenantStatusService
		extends ServiceImpl<SmsSendNodeTenantStatusDao, SmsSendNodeTenantStatusEntity> {

	@Autowired
	private GlobalPlatformService globalPlatformService;

	public void changeStatus(ChangeTenantSendNodeStatusIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		SmsSendNodeTenantStatusEntity existedEntity = getByTenantIdAndNodeId(in.getTenantId(), in.getSmsSendNodeId());
		
		SmsSendNodeTenantStatusEntity updateEntity = new SmsSendNodeTenantStatusEntity();
		updateEntity.setTenantId(in.getTenantId());
		updateEntity.setSmsSendNodeId(in.getSmsSendNodeId());
		updateEntity.setStatus(in.getStatus());
		if(existedEntity != null) {
			updateEntity.setId(existedEntity.getId());
			BaseEntityUtil.setModifyBaseEntityInfo(updateEntity, sysUser.getId(), sysUser.getName());
		} else {
			updateEntity.setId(ShortUUID.uuid());
			BaseEntityUtil.setCreateAndModifyBaseEntityInfo(updateEntity, sysUser.getId(), sysUser.getName());
		}
		this.saveOrUpdate(updateEntity);
	}

	public SmsSendNodeTenantStatusEntity getByTenantIdAndNodeId(Integer tenantId, String smsSendNodeId) {
		return baseMapper.selectOne(Wrappers.<SmsSendNodeTenantStatusEntity>lambdaQuery()
				.eq(SmsSendNodeTenantStatusEntity::getTenantId, tenantId)
				.eq(SmsSendNodeTenantStatusEntity::getSmsSendNodeId, smsSendNodeId));
	}

}
