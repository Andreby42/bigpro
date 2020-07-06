package com.thc.platform.modules.help.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.cmpt.DistributedLockCmpt;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.BaseEntityUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.help.dao.HelpWidgetBlockDao;
import com.thc.platform.modules.help.dto.HelpWidgetBlockChangeOrderIn;
import com.thc.platform.modules.help.entity.HelpWidgetBlockEntity;

@Service
public class HelpWidgetBlockService extends ServiceImpl<HelpWidgetBlockDao, HelpWidgetBlockEntity> {

	@Autowired
	private GlobalPlatformService globalPlatformService;
	@Autowired
	private DistributedLockCmpt distributedLockCmpt;
	
	public HelpWidgetBlockEntity add(String widgetId, String helpVerId, Integer type, String data, SysUserDto sysUser, Date currentTime) {
		HelpWidgetBlockEntity entity = new HelpWidgetBlockEntity();
		entity.setId(ShortUUID.uuid());
		entity.setWidgetId(widgetId);
		entity.setHelpVerId(helpVerId);
		entity.setType(type);
		entity.setData(data);
		BaseEntityUtil.setCreateAndModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName(), currentTime);
		
		HelpWidgetBlockEntity tailEntity = getTail(widgetId);
		if(tailEntity != null) {
			entity.setPrevId(tailEntity.getId());
		
			HelpWidgetBlockEntity updateTailEntity = new HelpWidgetBlockEntity();
			updateTailEntity.setId(tailEntity.getId());
			updateTailEntity.setNextId(entity.getId());
			BaseEntityUtil.setModifyBaseEntityInfo(updateTailEntity, sysUser.getId(), sysUser.getName(), currentTime);
			updateById(updateTailEntity);
		}
		save(entity);
		
		return entity;
	}
	
	private HelpWidgetBlockEntity getTail(String widgetId) {
		return baseMapper.selectOne(
				Wrappers.<HelpWidgetBlockEntity>lambdaQuery()
					.eq(HelpWidgetBlockEntity::getWidgetId, widgetId)
					.isNull(HelpWidgetBlockEntity::getNextId)
			);
	}
	
	private HelpWidgetBlockEntity getHead(String widgetId) {
		return baseMapper.selectOne(
				Wrappers.<HelpWidgetBlockEntity>lambdaQuery()
					.eq(HelpWidgetBlockEntity::getWidgetId, widgetId)
					.isNull(HelpWidgetBlockEntity::getPrevId)
			);
	}
	
//	private Integer countByWidgetId(String widgetId) {
//		return baseMapper.selectCount(
//				Wrappers.<HelpWidgetBlockEntity>lambdaQuery()
//					.eq(HelpWidgetBlockEntity::getWidgetId, widgetId)
//			);
//	}
	
	public void updateDataById(String id, String data, SysUserDto sysUser, Date currentTime) {
		HelpWidgetBlockEntity entity = new HelpWidgetBlockEntity();
		entity.setId(id);
		entity.setData(data);
		BaseEntityUtil.setModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName());
		updateById(entity);
	}
	
	public void changeOrder(HelpWidgetBlockChangeOrderIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		Date currentTime = new Date();
		
		HelpWidgetBlockEntity entity = getById(in.getId());
		if(entity == null)
			throw BEUtil.failNormal("更改顺序widget block不存在");
		
		String lockKey = HelpWidgetEditService.HELP_WIDGET_BLOCK_POINTER_LOCK_PREFIX + entity.getWidgetId();
		boolean locked = distributedLockCmpt.lock(lockKey, 1, TimeUnit.MINUTES, 5, 1, TimeUnit.SECONDS);
		if(locked) {
			// 更改节点上一个节点指针：nextId
			if(StringUtil.isNotEmpty(entity.getPrevId()))
				updateNextId(entity.getPrevId(), entity.getNextId(), sysUser, currentTime);
			
			// 更改节点下一个节点指针：prevId
			if(StringUtil.isNotEmpty(entity.getNextId()))
				updatePrevId(entity.getNextId(), entity.getPrevId(), sysUser, currentTime);
			
			// 插入队中或队尾
			if(StringUtil.isNotEmpty(in.getAfterId())) {
				HelpWidgetBlockEntity afterEntity = getById(in.getAfterId());
				if(afterEntity == null)
					throw BEUtil.failNormal("参照排序widget block不存在");
				
				// 修改插入参照节点指针信息: nextId
				updateNextId(afterEntity.getId(), entity.getId(), sysUser, currentTime);
				// 修改插入节点指针信息: prevId, nextId
				updatePointer(entity.getId(), afterEntity.getId(), afterEntity.getNextId(), sysUser, currentTime);
				
				// 修改下一个节点指针信息: prevId
				if(StringUtil.isNotEmpty(afterEntity.getNextId())) 
					updatePrevId(afterEntity.getNextId(), entity.getId(), sysUser, currentTime);
				
			} else {// 放队头
				HelpWidgetBlockEntity headEntity = getHead(entity.getWidgetId());
				if(headEntity == null) 
					return;
				
				// 修改头节点指针信息
				updatePrevId(headEntity.getId(), entity.getId(), sysUser, currentTime);
				// 修改插入节点指针信息
				updatePointer(entity.getId(), null, headEntity.getId(), sysUser, currentTime);
			}
			distributedLockCmpt.release(lockKey);
		} else {
			throw BEUtil.failNormal("修改冲突，请稍后再试");
		}
	}
	
	private void updatePointer(String id, String prevId, String nextId, SysUserDto sysUser, Date currentTime) {
		LambdaUpdateWrapper<HelpWidgetBlockEntity> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.set(HelpWidgetBlockEntity::getPrevId, prevId)
			.set(HelpWidgetBlockEntity::getNextId, nextId)
			.set(HelpWidgetBlockEntity::getModifyUserId, sysUser.getId())
			.set(HelpWidgetBlockEntity::getModifyUserName, sysUser.getName())
			.set(HelpWidgetBlockEntity::getModifyTime, currentTime)
			.eq(HelpWidgetBlockEntity::getId, id);
		
		this.update(updateWrapper);
	}
	
	public void updatePrevId(String id, String prevId, SysUserDto sysUser, Date currentTime) {
		LambdaUpdateWrapper<HelpWidgetBlockEntity> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.set(HelpWidgetBlockEntity::getPrevId, prevId)
			.set(HelpWidgetBlockEntity::getModifyUserId, sysUser.getId())
			.set(HelpWidgetBlockEntity::getModifyUserName, sysUser.getName())
			.set(HelpWidgetBlockEntity::getModifyTime, currentTime)
			.eq(HelpWidgetBlockEntity::getId, id);
		
		this.update(updateWrapper);
	}
	
	public void updateNextId(String id, String nextId, SysUserDto sysUser, Date currentTime) {
		LambdaUpdateWrapper<HelpWidgetBlockEntity> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.set(HelpWidgetBlockEntity::getNextId, nextId)
			.set(HelpWidgetBlockEntity::getModifyUserId, sysUser.getId())
			.set(HelpWidgetBlockEntity::getModifyUserName, sysUser.getName())
			.set(HelpWidgetBlockEntity::getModifyTime, currentTime)
			.eq(HelpWidgetBlockEntity::getId, id);
		
		this.update(updateWrapper);
	}
	
	public List<HelpWidgetBlockEntity> getByWidgetId(String widgetId) {
		List<HelpWidgetBlockEntity> entities =  
				baseMapper.selectList(Wrappers.<HelpWidgetBlockEntity>lambdaQuery()
						.eq(HelpWidgetBlockEntity::getWidgetId, widgetId));
		
		return ordered(entities);
	}
	
	private List<HelpWidgetBlockEntity> ordered(List<HelpWidgetBlockEntity> entities) {
		if(entities == null || entities.size() == 0)
			return entities;
		
		Map<String, HelpWidgetBlockEntity> blockMap = new HashMap<>();
		HelpWidgetBlockEntity headEntity = null;
		for(HelpWidgetBlockEntity entity : entities) {
			blockMap.put(entity.getId(), entity);
			if(StringUtil.isEmpty(entity.getPrevId()))
				headEntity = entity;
		}
		
		if(headEntity == null)
			throw BEUtil.failNormal("数据节点顺序错误。");
		
		int count = 0;
		int length = entities.size();
		
		List<HelpWidgetBlockEntity> orderedEntitits = new ArrayList<>();
		orderedEntitits.add(headEntity);
		String nextId = headEntity.getNextId();
		
		while(StringUtil.isNotEmpty(nextId)) {
			HelpWidgetBlockEntity tempEntity = blockMap.get(nextId);
			orderedEntitits.add(tempEntity);
			nextId = tempEntity.getNextId();
			
			// 防止错误数据造成死循环
			count ++;
			if(count > length)
				break;
		}
		return orderedEntitits;
	}
	
	public void deleteByWidgetId(String widgetId) {
		baseMapper.delete(
					Wrappers.<HelpWidgetBlockEntity>lambdaQuery()
						.eq(HelpWidgetBlockEntity::getWidgetId, widgetId)
				);
	}
	
	public void deleteByHelpVerId(String helpVerId) {
		baseMapper.delete(
				Wrappers.<HelpWidgetBlockEntity>lambdaQuery()
					.eq(HelpWidgetBlockEntity::getHelpVerId, helpVerId)
			);
	}
	
}
