package com.thc.platform.modules.help.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.BaseEntityUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.modules.help.dao.HelpPageVideoRelDao;
import com.thc.platform.modules.help.entity.HelpPageVideoRelEntity;

@Service
public class HelpPageVideoRelService extends ServiceImpl<HelpPageVideoRelDao, HelpPageVideoRelEntity> {

	public void batchAdd(List<String> helpVideoIds, String pageId, Date currentTime, SysUserDto sysUser) {
		remove(Wrappers.<HelpPageVideoRelEntity>lambdaQuery().eq(HelpPageVideoRelEntity::getPageId, pageId));
		
		if(helpVideoIds != null && helpVideoIds.size() > 0) {
			List<HelpPageVideoRelEntity> pvRelEntities = new ArrayList<>();
			for(String videoId : helpVideoIds) {
				HelpPageVideoRelEntity pvRelEntity = new HelpPageVideoRelEntity(ShortUUID.uuid(), pageId, videoId);
				BaseEntityUtil.setCreateBaseEntityInfo(pvRelEntity, sysUser.getId(), sysUser.getName(), currentTime);
				pvRelEntities.add(pvRelEntity);
			}
			saveBatch(pvRelEntities);
		}
	}
	
	public List<HelpPageVideoRelEntity> getByPageId(String pageId) {
		return baseMapper.selectList(
				Wrappers.<HelpPageVideoRelEntity>lambdaQuery()
					.eq(HelpPageVideoRelEntity::getPageId, pageId)
			);
	}
	
	public void deleteByPageId(String pageId) {
		update(Wrappers.<HelpPageVideoRelEntity>lambdaUpdate()
					.set(HelpPageVideoRelEntity::getDeleteStatus, HelpPageVideoRelEntity.DELETE_STATUS_YES)
					.eq(HelpPageVideoRelEntity::getPageId, pageId)
			);
	}
	
}
