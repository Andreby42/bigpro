package com.thc.platform.modules.help.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.BaseEntityUtil;
import com.thc.platform.common.util.DateUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.help.dao.HelpVideoDao;
import com.thc.platform.modules.help.dto.HelpVideoListIn;
import com.thc.platform.modules.help.dto.HelpVideoSaveIn;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.entity.HelpVideoEntity;

@Service
public class HelpVideoService extends ServiceImpl<HelpVideoDao, HelpVideoEntity> {

	@Autowired
	private GlobalPlatformService globalPlatformService;
	@Autowired
	private HelpVerService helpVerService;
	
	public void save(HelpVideoSaveIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		Date currentTime = new Date();
		
		String helpVerId = null;
		if(StringUtil.isNotEmpty(in.getHelpId())) {
			HelpVerEntity verEntity = helpVerService.getByHelpIdAndVer(in.getHelpId(), in.getHelpVer());
			if(verEntity == null)
				throw BEUtil.failNormal("系统版本不存在");
			
			helpVerId = verEntity.getId();
		}
		
		if(StringUtil.isEmpty(in.getId()))
			add(in, helpVerId, currentTime, sysUser);
		else 
			update(in, helpVerId, currentTime, sysUser);
	}
	
	private void add(HelpVideoSaveIn in, String helpVerId, Date currentTime, SysUserDto sysUser) {
		HelpVideoEntity entity = new HelpVideoEntity();
		entity.setId(ShortUUID.uuid());
		entity.setName(in.getName());
		entity.setVideoUrl(in.getVideoUrl());
		entity.setVideoThumbnailUrl(in.getVideoThumbnailUrl());
		entity.setVideoCaptionFileUrl(in.getVideoCaptionFileUrl());
		entity.setHelpVerId(helpVerId);
		entity.setTags(getTagsString(in.getTags()));
		entity.setBriefIntroduction(in.getBriefIntroduction());
		BaseEntityUtil.setCreateAndModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName(), currentTime);
		this.save(entity);
	}
	
	private String getTagsString(List<String> tags) {
		if(tags != null && tags.size() > 0) {
			StringBuilder sb = new StringBuilder();
			int count = 0;
			for(String tag : tags) {
				if(count > 0)
					sb.append(",");
				
				sb.append(tag);
				count ++;
			}
			return sb.toString();
		} else {
			return null;
		}
	}
	
	private void update(HelpVideoSaveIn in, String helpVerId, Date currentTime, SysUserDto sysUser) {
		HelpVideoEntity entity = getById(in.getId());
		if(entity == null)
			throw BEUtil.failNormal("更新实体不存在");
		
		this.update(
				Wrappers.<HelpVideoEntity>lambdaUpdate()
					.set(HelpVideoEntity::getName, in.getName())
					.set(HelpVideoEntity::getVideoUrl, in.getVideoUrl())
					.set(HelpVideoEntity::getVideoThumbnailUrl, in.getVideoThumbnailUrl())
					.set(HelpVideoEntity::getVideoCaptionFileUrl, in.getVideoCaptionFileUrl())
					.set(HelpVideoEntity::getHelpVerId, helpVerId)
					.set(HelpVideoEntity::getTags, getTagsString(in.getTags()))
					.set(HelpVideoEntity::getBriefIntroduction, in.getBriefIntroduction())
					.set(HelpVideoEntity::getModifyUserId, sysUser.getId())
					.set(HelpVideoEntity::getModifyUserName, sysUser.getName())
					.set(HelpVideoEntity::getModifyTime, currentTime)
					.eq(HelpVideoEntity::getId, in.getId())
			);
	}
	
	public PageOut<HelpVideoEntity> listPage(HelpVideoListIn in) {
		LambdaQueryWrapper<HelpVideoEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HelpVideoEntity::getDeleteStatus, HelpVideoEntity.DELETE_STATUS_NO);
		
		if(StringUtil.isNotEmpty(in.getKeyword())) {
			wrapper.like(HelpVideoEntity::getName, in.getKeyword())
				.or()
				.like(HelpVideoEntity::getTags, in.getKeyword());
		}
		
		if(StringUtil.isNotEmpty(in.getUploadStartTime())) {
			Date startTime = DateUtil.parseDefaultPattern(in.getUploadStartTime());
			wrapper.ge(HelpVideoEntity::getCreateTime, startTime);
		}
		
		if(StringUtil.isNotEmpty(in.getUploadEndTime())) {
			Date endTime = DateUtil.parseDefaultPattern(in.getUploadEndTime());
			wrapper.le(HelpVideoEntity::getCreateTime, endTime);
		}
		
		Integer totalCount = baseMapper.selectCount(wrapper);
		
		wrapper.orderByDesc(HelpVideoEntity::getCreateTime);
		
		wrapper.last("limit " + in.getOffset() + ", " + in.getPagesize());
		List<HelpVideoEntity> items = baseMapper.selectList(wrapper);
		
		return new PageOut<HelpVideoEntity>(items, totalCount);
	}
	
	public void updateDeleteStatus(String id) {
		if(StringUtil.isEmpty(id))
			return;
		
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		Date currentTime = new Date();
		
		HelpVideoEntity updateEntity = new HelpVideoEntity();
		updateEntity.setId(id);
		updateEntity.setDeleteStatus(HelpVideoEntity.DELETE_STATUS_YES);
		BaseEntityUtil.setModifyBaseEntityInfo(updateEntity, sysUser.getId(), sysUser.getName(), currentTime);
		
		updateById(updateEntity);
	}
	
}
