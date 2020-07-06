package com.thc.platform.modules.help.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.BaseEntityUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.modules.help.dao.HelpWidgetDao;
import com.thc.platform.modules.help.entity.HelpWidgetEntity;

@Service
public class HelpWidgetService extends ServiceImpl<HelpWidgetDao, HelpWidgetEntity> {

	public HelpWidgetEntity getByHelpVerIdAndMenuId(String helpVerId, String menuId) {
		return baseMapper.selectOne(
				Wrappers.<HelpWidgetEntity>lambdaQuery()
					.eq(HelpWidgetEntity::getHelpVerId, helpVerId)
					.eq(HelpWidgetEntity::getMenuId, menuId)
			);
	}
	
	public HelpWidgetEntity add(String helpVerId, String menuId, SysUserDto sysUser, Date currentTime) {
		HelpWidgetEntity entity = new HelpWidgetEntity();
		
		entity.setId(ShortUUID.uuid());
		entity.setHelpVerId(helpVerId);
		entity.setMenuId(menuId);
		BaseEntityUtil.setCreateAndModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName(), currentTime);
		
		save(entity);
		
		return entity;
	}
	
	public void deleteByHelpVerId(String helpVerId) {
		baseMapper.delete(
				Wrappers.<HelpWidgetEntity>lambdaQuery()
					.eq(HelpWidgetEntity::getHelpVerId, helpVerId)
			);
	}
	
}
