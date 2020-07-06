package com.thc.platform.modules.sms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.sms.dao.SysTemplateDao;
import com.thc.platform.modules.sms.dto.SysTemplateSaveIn;
import com.thc.platform.modules.sms.dto.SysTemplateListIn;
import com.thc.platform.modules.sms.entity.SysTemplateEntity;

@Service
public class SysTemplateService extends ServiceImpl<SysTemplateDao, SysTemplateEntity> {

	@Autowired
	private GlobalPlatformService globalPlatformService;
	
	public void save(SysTemplateSaveIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		Date currentTime = new Date();
		SysTemplateEntity entity = new SysTemplateEntity();
		
		if(StringUtil.isEmpty(in.getId())) {
			entity.setId(ShortUUID.uuid());
			entity.setCreatorId(sysUser.getId());
			entity.setCreatorName(sysUser.getName());
			entity.setCreateTime(currentTime);
		} else {
			entity.setId(in.getId());
		}
		
		entity.setCode(in.getCode())
			.setSmsType(in.getSmsType())
			.setName(in.getName())
			.setContent(in.getContent())
			.setModifyUserId(sysUser.getId())
			.setModifyUserName(sysUser.getName())
			.setModifyTime(currentTime);
		
		this.saveOrUpdate(entity);
	}
	
	public PageOut<SysTemplateEntity> listPage(SysTemplateListIn in) {
		
		Integer totalCount = baseMapper.listPageCount(
				in.getTenantId(), in.getCode(), in.getSmsType(), in.getName());
		
		
		List<SysTemplateEntity> items = baseMapper.listPage(in.getTenantId()
				, in.getCode(), in.getSmsType(), in.getName()
				, in.getOffset(), in.getPagesize());
		
		return new PageOut<SysTemplateEntity>(items, totalCount);
	}
	
	public SysTemplateEntity getByCode(String code) {
		return baseMapper.selectOne(
				Wrappers.<SysTemplateEntity>lambdaQuery()
					.eq(SysTemplateEntity::getCode, code));
	}
	
}
