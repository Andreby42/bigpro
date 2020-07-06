package com.thc.platform.modules.sms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.sms.dao.YtxTemplateDao;
import com.thc.platform.modules.sms.dto.YtxTemplateListIn;
import com.thc.platform.modules.sms.dto.YtxTemplateSaveIn;
import com.thc.platform.modules.sms.entity.YtxTemplateEntity;

/** 云通讯短信模板服务 */
@Service
public class YtxTemplateService extends ServiceImpl<YtxTemplateDao, YtxTemplateEntity> {

	@Autowired
	private GlobalPlatformService globalPlatformService;
	
	/** 保存 */
	public void save(YtxTemplateSaveIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		Date currentTime = new Date();
		
		YtxTemplateEntity entity = new YtxTemplateEntity();
		
		if(StringUtil.isEmpty(in.getId())) {
			entity.setId(ShortUUID.uuid());
			entity.setCreatorId(sysUser.getId());
			entity.setCreatorName(sysUser.getName());
			entity.setCreateTime(currentTime);
		} else {
			entity.setId(in.getId());
		}
		
		entity.setTemplateId(in.getTemplateId())
			.setType(in.getType())
			.setAppId(in.getAppId())
			.setSignTypeId(in.getSignTypeId())
			.setNotes(in.getNotes())
			.setModifyUserId(sysUser.getId())
			.setModifyUserName(sysUser.getName())
			.setModifyTime(currentTime);
	
		this.saveOrUpdate(entity);
		
	}
	
	/** 按应用ID查询  */
	public List<YtxTemplateEntity> getByAppId(String appId) {
		return baseMapper.selectList(
				Wrappers.<YtxTemplateEntity>lambdaQuery()
					.eq(YtxTemplateEntity::getAppId, appId)
			);
	}
	
	/** 分页查询  */
	public PageOut<YtxTemplateEntity> listPage(YtxTemplateListIn in) {
		
		LambdaQueryWrapper<YtxTemplateEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.like(YtxTemplateEntity::getAppId, in.getAppId());
		
		if(StringUtil.isNotEmpty(in.getTemplateId()))
			wrapper.like(YtxTemplateEntity::getTemplateId, in.getTemplateId());
		
		if(in.getType() != null)
			wrapper.like(YtxTemplateEntity::getType, in.getType());
		
		if(StringUtil.isNotEmpty(in.getSignTypeId()))
			wrapper.like(YtxTemplateEntity::getSignTypeId, in.getSignTypeId());
		
		Integer totalCount = baseMapper.selectCount(wrapper);
		
		wrapper.last("limit " + in.getOffset() + ", " + in.getPagesize());
		
		List<YtxTemplateEntity> items = baseMapper.selectList(wrapper);
		
		return new PageOut<YtxTemplateEntity>(items, totalCount);
	}
	
	/** 按应用ID统计数量  */
	public Integer countByAppId(String appId) {
		return baseMapper.selectCount(
				Wrappers.<YtxTemplateEntity>lambdaQuery()
					.eq(YtxTemplateEntity::getAppId, appId));
	}

}
