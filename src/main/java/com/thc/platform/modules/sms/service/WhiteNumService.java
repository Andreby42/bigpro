package com.thc.platform.modules.sms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.sms.dao.WhiteNumDao;
import com.thc.platform.modules.sms.dto.WhiteNumAddIn;
import com.thc.platform.modules.sms.dto.WhiteNumListIn;
import com.thc.platform.modules.sms.entity.WhiteNumEntity;

/** 白名单服务 */
@Service
public class WhiteNumService extends ServiceImpl<WhiteNumDao, WhiteNumEntity> {

	@Autowired
	private GlobalPlatformService globalPlatformService;
	
	/**
	 * 新增白名单号码
	 */
	@Transactional
	public void add(WhiteNumAddIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		
		deleteMobile(in.getYtxAppId(), in.getMobile());
		
		WhiteNumEntity entity = new WhiteNumEntity();
		entity.setYtxAppId(in.getYtxAppId())
			.setMobile(in.getMobile())
			.setNotes(in.getNotes())
			.setCreatorId(sysUser.getId())
			.setCreatorName(sysUser.getName())
			.setCreateTime(new Date());
		
		this.save(entity);
	}
	
	/**
	 * 删除白名单手机号
	 * @param ytxAppId 云通讯应用ID
	 * @param mobile 手机号码
	 */
	public void deleteMobile(String ytxAppId, String mobile) {
		baseMapper.delete(
				Wrappers.<WhiteNumEntity>lambdaQuery()
					.eq(WhiteNumEntity::getYtxAppId, ytxAppId)
					.eq(WhiteNumEntity::getMobile, mobile)
			);
	}
	
	/**
	 * 分页查询
	 */
	public PageOut<WhiteNumEntity> list(WhiteNumListIn in) { 
		LambdaQueryWrapper<WhiteNumEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(WhiteNumEntity::getYtxAppId, in.getYtxAppId());
		
		if(StringUtil.isNotEmpty(in.getKeyword()))
			wrapper.like(WhiteNumEntity::getMobile, in.getKeyword())
				.or()
				.like(WhiteNumEntity::getNotes, in.getKeyword());
		
		Integer totalCount = baseMapper.selectCount(wrapper);
		
		wrapper.last("limit " + in.getOffset() + ", " + in.getPagesize());
		
		List<WhiteNumEntity> items = baseMapper.selectList(wrapper);
		
		return new PageOut<WhiteNumEntity>(items, totalCount);
	}

	/**
	 * 按业务ID统计数据
	 * @param ytxAppId 云通讯应用ID
	 * @param mobile 手机号
	 * @return 统计数量
	 */
	public Integer countByBusiId(String ytxAppId, String mobile) {
		return baseMapper.selectCount(
				Wrappers.<WhiteNumEntity>lambdaQuery()
					.eq(WhiteNumEntity::getYtxAppId, ytxAppId)
					.eq(WhiteNumEntity::getMobile, mobile)
				);
	}
}
