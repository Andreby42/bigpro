package com.thc.platform.modules.sms.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.modules.sms.dao.ReceiveRecordDao;
import com.thc.platform.modules.sms.entity.ReceiveRecordEntity;

@Service
public class ReceiveRecordService extends ServiceImpl<ReceiveRecordDao, ReceiveRecordEntity> {

	
	public ReceiveRecordEntity getByBusiId(Integer tenantId, String appCode, String appSerialNum) {
		return baseMapper.selectOne(
				Wrappers.<ReceiveRecordEntity>lambdaQuery()
					.eq(ReceiveRecordEntity::getTenantId, tenantId)
					.eq(ReceiveRecordEntity::getAppCode, appCode)
					.eq(ReceiveRecordEntity::getAppSerialNum, appSerialNum)
			);
	}
	
	public Integer countByBusiId(Integer tenantId, String appCode, String appSerialNum) {
		return baseMapper.selectCount(
				Wrappers.<ReceiveRecordEntity>lambdaQuery()
					.eq(ReceiveRecordEntity::getTenantId, tenantId)
					.eq(ReceiveRecordEntity::getAppCode, appCode)
					.eq(ReceiveRecordEntity::getAppSerialNum, appSerialNum)
			);
	}
}
