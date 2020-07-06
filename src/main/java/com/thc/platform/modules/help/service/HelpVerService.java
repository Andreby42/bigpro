package com.thc.platform.modules.help.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.modules.help.dao.HelpVerDao;
import com.thc.platform.modules.help.entity.HelpVerEntity;

@Service
public class HelpVerService extends ServiceImpl<HelpVerDao, HelpVerEntity> {

	public List<HelpVerEntity> getByHelpId(String helpId) {
		return baseMapper.selectList(
				Wrappers.<HelpVerEntity>lambdaQuery()
					.eq(HelpVerEntity::getHelpId, helpId)
					.eq(HelpVerEntity::getDeleteStatus, HelpVerEntity.DELETE_STATUS_NO)
			);
	}
	
	public PageOut<HelpVerEntity> listPageByHelpId(String helpId, Integer offset, Integer pagesize) {
		LambdaQueryWrapper<HelpVerEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HelpVerEntity::getHelpId, helpId)
			.eq(HelpVerEntity::getDeleteStatus, HelpVerEntity.DELETE_STATUS_NO);
		
		Integer totalCount = baseMapper.selectCount(wrapper);
		
		wrapper.orderByDesc(HelpVerEntity::getId);
		wrapper.last("limit " + offset + ", " + pagesize);
		List<HelpVerEntity> items = baseMapper.selectList(wrapper);
		
		return new PageOut<HelpVerEntity>(items, totalCount);
	}
	
	public HelpVerEntity getByHelpIdAndVer(String helpId, String ver) {
		LambdaQueryWrapper<HelpVerEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HelpVerEntity::getHelpId, helpId)
			.eq(HelpVerEntity::getVer, ver)
			.eq(HelpVerEntity::getDeleteStatus, HelpVerEntity.DELETE_STATUS_NO);
		
		return baseMapper.selectOne(wrapper);
	}
	
}
