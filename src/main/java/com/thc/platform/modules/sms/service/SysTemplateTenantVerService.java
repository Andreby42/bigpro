package com.thc.platform.modules.sms.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.sms.dao.SysTemplateTenantVerDao;
import com.thc.platform.modules.sms.dto.SysTemplateListIn;
import com.thc.platform.modules.sms.dto.SysTemplateTenantVerSaveIn;
import com.thc.platform.modules.sms.entity.SysTemplateEntity;
import com.thc.platform.modules.sms.entity.SysTemplateTenantVerEntity;

@Service
public class SysTemplateTenantVerService extends ServiceImpl<SysTemplateTenantVerDao, SysTemplateTenantVerEntity> {

	@Autowired
	private GlobalPlatformService globalPlatformService;
	@Autowired
	private SysTemplateService sysTemplateService;
	
	public SysTemplateTenantVerEntity get(String sysTemplateId, Integer tenantId) {
		if(tenantId == null)
			return null;
		
		return baseMapper.selectOne(
				Wrappers.<SysTemplateTenantVerEntity>lambdaQuery()
					.eq(SysTemplateTenantVerEntity::getSysTemplateId, sysTemplateId)
					.eq(SysTemplateTenantVerEntity::getTenantId, tenantId)
			);
	}
	
	public void delete(String sysTemplateId, Integer tenantId) {
		baseMapper.delete(
				Wrappers.<SysTemplateTenantVerEntity>lambdaQuery()
					.eq(SysTemplateTenantVerEntity::getSysTemplateId, sysTemplateId)
					.eq(SysTemplateTenantVerEntity::getTenantId, tenantId)
			);
	}
	
	public Integer countTenantVer(String sysTemplateId) {
		return baseMapper.selectCount(
					Wrappers.<SysTemplateTenantVerEntity>lambdaQuery()
					.eq(SysTemplateTenantVerEntity::getSysTemplateId, sysTemplateId)
				);
	}
	
	public void save(SysTemplateTenantVerSaveIn in) {
		SysTemplateEntity sysTemplate = sysTemplateService.getById(in.getSysTemplateId());
		if(sysTemplate == null)
			throw BEUtil.failNormal("系统模版不存在");
		
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		Date currentTime = new Date();
		
		SysTemplateTenantVerEntity entity = get(in.getSysTemplateId(), in.getTenantId());
		
		if(entity == null) {
			entity = new SysTemplateTenantVerEntity();
			
			entity.setCreatorId(sysUser.getId())
				.setCreatorName(sysUser.getName())
				.setCreateTime(currentTime);
		}
		
		entity.setSysTemplateId(in.getSysTemplateId())
			.setTenantId(in.getTenantId())
			.setContent(in.getContent())
			.setModifyUserId(sysUser.getId())
			.setModifyUserName(sysUser.getName())
			.setModifyTime(currentTime);
		
		this.saveOrUpdate(entity);
	}

	public PageOut<Map<String, Object>> listPage(SysTemplateListIn in) {
		
		LambdaQueryWrapper<SysTemplateTenantVerEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(SysTemplateTenantVerEntity::getTenantId, in.getTenantId());
		Integer totalCount = baseMapper.listPageCount(in.getTenantId()
				, in.getCode(), in.getSmsType(), in.getName());
		
		List<Map<String, Object>> items = baseMapper.listPage(
				in.getTenantId(), in.getCode(), in.getSmsType(), in.getName()
				, in.getOffset(), in.getPagesize());
		
		return new PageOut<Map<String, Object>>(items, totalCount);
	}
	
}
