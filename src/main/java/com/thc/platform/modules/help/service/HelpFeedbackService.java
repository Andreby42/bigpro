package com.thc.platform.modules.help.service;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.DateUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.api.GlobalPlatformApi;
import com.thc.platform.external.dto.TenantInternalConfigDto;
import com.thc.platform.modules.help.dao.HelpFeedbackDao;
import com.thc.platform.modules.help.dto.HelpFeedbackIn;
import com.thc.platform.modules.help.dto.HelpFeedbackListIn;
import com.thc.platform.modules.help.entity.HelpFeedbackEntity;
import com.thc.platform.modules.help.entity.HelpMenuEntity;
import com.thc.platform.modules.help.entity.HelpVerEntity;

@Service
public class HelpFeedbackService extends ServiceImpl<HelpFeedbackDao, HelpFeedbackEntity> {

	private Logger logger = LoggerFactory.getLogger(HelpFeedbackService.class);
	
	@Autowired
	private GlobalPlatformApi api;
	@Autowired
	private HelpMenuService helpMenuService;
	@Autowired
	private HelpVerService verService;
	
	public PageOut<HelpFeedbackEntity> listPage(HelpFeedbackListIn in) {
		LambdaQueryWrapper<HelpFeedbackEntity> wrapper = Wrappers.lambdaQuery();
		
		if(StringUtil.isNotEmpty(in.getStartTime())) {
			Date startTime = DateUtil.parseDefaultPattern(in.getStartTime());
			wrapper.ge(HelpFeedbackEntity::getCreateTime, startTime);
		}
		
		if(StringUtil.isNotEmpty(in.getEndTime())) {
			Date endTime = DateUtil.parseDefaultPattern(in.getEndTime());
			wrapper.le(HelpFeedbackEntity::getCreateTime, endTime);
		}
		
		Integer totalCount = baseMapper.selectCount(wrapper);
		
		wrapper.orderByDesc(HelpFeedbackEntity::getCreateTime)
			.last("limit " + in.getOffset() + ", " + in.getPagesize());
		
		List<HelpFeedbackEntity> items = baseMapper.selectList(wrapper);
		
		return new PageOut<HelpFeedbackEntity>(items, totalCount);
	}
	
	public void add(HelpFeedbackIn in) {
		HelpFeedbackEntity entity = new HelpFeedbackEntity();
		entity.setId(ShortUUID.uuid());
		entity.setTenantId(in.getTenantId());
		entity.setMenuId(in.getMenuId());
		entity.setContent(in.getContent());
		entity.setCreatorMobile(in.getCreatorMobile());
		entity.setCreatorId(in.getCreatorId());
		entity.setCreatorName(in.getCreatorName());
		entity.setCreateTime(new Date());
		
		String tenantName = null;
		try {
			Api<TenantInternalConfigDto> result = api.getTenantsWithConfigs(in.getTenantId());
			tenantName = result.getData().getGroupName();
		} catch (Exception e) {
			logger.error("获取租户信息错误", e);
		}
		entity.setTenantName(tenantName);
		
		HelpMenuEntity menuEntity = helpMenuService.getById(in.getMenuId());
		if(menuEntity != null) {
			entity.setMenuName(menuEntity.getName());
			HelpVerEntity verEntity = verService.getById(menuEntity.getHelpVerId());
			entity.setHelpId(verEntity.getHelpId());
			entity.setHelpVer(verEntity.getVer());
		}
		
		this.save(entity);
	}
	
}
