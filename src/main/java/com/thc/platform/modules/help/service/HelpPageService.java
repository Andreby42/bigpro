package com.thc.platform.modules.help.service;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.BaseEntityUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.modules.help.dao.HelpPageDao;
import com.thc.platform.modules.help.entity.HelpPageEntity;

@Service
public class HelpPageService extends ServiceImpl<HelpPageDao, HelpPageEntity> {

	public HelpPageEntity add(String richText, String helpVerId, SysUserDto sysUser, Date currentTime) {
		HelpPageEntity entity = new HelpPageEntity();
		
		entity.setId(ShortUUID.uuid());
		entity.setRichText(richText);
		entity.setCreateHelpVerId(helpVerId);
		BaseEntityUtil.setCreateAndModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName(), currentTime);
		
		save(entity);
		
		return entity;
	}
	
	public HelpPageEntity update(String pageId, String richText, SysUserDto sysUser, Date currentTime) {
		HelpPageEntity entity = new HelpPageEntity();
		
		entity.setId(pageId);
		entity.setRichText(richText);
		BaseEntityUtil.setModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName(), currentTime);
		
		this.updateById(entity);
		
		return entity;
	}
	
}
