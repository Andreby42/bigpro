package com.thc.platform.modules.help.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.BaseEntityUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.modules.help.dao.HelpMenuDao;
import com.thc.platform.modules.help.dto.HelpMenuSearchIn;
import com.thc.platform.modules.help.dto.HelpMenuTreeOut;
import com.thc.platform.modules.help.dto.HelpSaveIn;
import com.thc.platform.modules.help.entity.HelpMenuEntity;
import com.thc.platform.modules.help.entity.HelpVerEntity;

@Service
public class HelpMenuService extends ServiceImpl<HelpMenuDao, HelpMenuEntity> {

	@Autowired
	private HelpVerService helpVerService;
	@Autowired
	private HelpVerService verService;
	
	public HelpMenuTreeOut loadMenuTree(String helpId, String ver) {
		HelpVerEntity helpVerEntity = helpVerService.getByHelpIdAndVer(helpId, ver);
		if(helpVerEntity == null)
			throw BEUtil.failNormal("帮助版本不存在");
		
		List<HelpMenuTreeOut.MenuNodeOut> menuNodeOuts = new ArrayList<>();
		List<HelpMenuEntity> menuEntities = getByHelpVerIdAndRootId(helpVerEntity.getId(), null);
		for(HelpMenuEntity entity : menuEntities) 
			menuNodeOuts.add(loadChild(entity, null));
		
		HelpMenuTreeOut out = new HelpMenuTreeOut();
		out.setHelpId(helpVerEntity.getHelpId());
		out.setVer(helpVerEntity.getVer());
		out.setMenus(menuNodeOuts);
		
		return out;
	}
	
//	public List<HelpMenuEntity> getRootByHelpVerId(String helpVerId) {
//		LambdaQueryWrapper<HelpMenuEntity> wrapper = Wrappers.lambdaQuery();
//		wrapper.eq(HelpMenuEntity::getHelpVerId, helpVerId)
//			.isNull(HelpMenuEntity::getRootId)
//			.eq(HelpMenuEntity::getDeleteStatus, HelpMenuEntity.DELETE_STATUS_NO);
//		
//		List<HelpMenuEntity> entities = baseMapper.selectList(wrapper);
//		return ordered(entities);
//	}
	
	private List<HelpMenuEntity> ordered(List<HelpMenuEntity> entities) {
		if(entities == null || entities.size() == 0)
			return entities;
		
		Map<String, HelpMenuEntity> blockMap = new HashMap<>();
		HelpMenuEntity headEntity = null;
		for(HelpMenuEntity entity : entities) {
			blockMap.put(entity.getId(), entity);
			if(StringUtil.isEmpty(entity.getPrevId()))
				headEntity = entity;
		}
		
		if(headEntity == null)
			throw BEUtil.failNormal("数据节点顺序错误。");
		
		int count = 0;
		int length = entities.size();
		
		List<HelpMenuEntity> orderedEntitits = new ArrayList<>();
		orderedEntitits.add(headEntity);
		String nextId = headEntity.getNextId();
		
		while(StringUtil.isNotEmpty(nextId)) {
			HelpMenuEntity tempEntity = blockMap.get(nextId);
			orderedEntitits.add(tempEntity);
			nextId = tempEntity.getNextId();
			
			// 防止错误数据造成死循环
			count ++;
			if(count > length)
				break;
		}
		return orderedEntitits;
	}
	
	public List<HelpMenuEntity> getByHelpVerIdAndRootId(String helpVerId, String rootId) {
		LambdaQueryWrapper<HelpMenuEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HelpMenuEntity::getHelpVerId, helpVerId)
			.eq(HelpMenuEntity::getDeleteStatus, HelpMenuEntity.DELETE_STATUS_NO);
		
		if(StringUtil.isEmpty(rootId))
			wrapper.isNull(HelpMenuEntity::getRootId);
		else
			wrapper.eq(HelpMenuEntity::getRootId, rootId);
			
		List<HelpMenuEntity> entities = baseMapper.selectList(wrapper);
		return ordered(entities);
	}
	
	private HelpMenuTreeOut.MenuNodeOut loadChild(HelpMenuEntity entity, String rootId) {
		HelpMenuTreeOut.MenuNodeOut menuNodeOut = new HelpMenuTreeOut.MenuNodeOut();
		menuNodeOut.setId(entity.getId());
		menuNodeOut.setRootId(rootId);
		menuNodeOut.setName(entity.getName());
		
		List<HelpMenuEntity> children = getByHelpVerIdAndRootId(entity.getHelpVerId(), entity.getId());
		if(children != null && children.size() > 0) {
			List<HelpMenuTreeOut.MenuNodeOut> menuNodeOuts = new ArrayList<>();
			menuNodeOut.setNodes(menuNodeOuts);
			for(HelpMenuEntity child : children) 
				menuNodeOuts.add(loadChild(child, entity.getId()));
		}
		return menuNodeOut;
	}
	
	public void update(String id, String name, String pageId, SysUserDto sysUser, Date currentTime) {
		HelpMenuEntity entity = new HelpMenuEntity();
		entity.setId(id);
		entity.setName(name);
		entity.setPageId(pageId);
		BaseEntityUtil.setModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName(), currentTime);
		updateById(entity);
	}
	
	public int countByPageId(String pageId) {
		return baseMapper.selectCount(
				Wrappers.<HelpMenuEntity>lambdaQuery()
					.eq(HelpMenuEntity::getPageId, pageId)
					.eq(HelpMenuEntity::getDeleteStatus, HelpMenuEntity.DELETE_STATUS_NO)
			);
	}

	public String add(HelpSaveIn in, String helpVerId, String pageId, SysUserDto sysUser, Date currentTime) {
		HelpMenuEntity entity = new HelpMenuEntity();
		entity.setId(ShortUUID.uuid());
		entity.setRootId(in.getRootMenuId());
		entity.setName(in.getMenuName());
		entity.setHelpVerId(helpVerId);
		entity.setPageId(pageId);
		BaseEntityUtil.setCreateAndModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName(), currentTime);
		
		
		HelpMenuEntity tailEntity = getTail(in.getRootMenuId(), helpVerId); 
		if(tailEntity != null) {
			entity.setPrevId(tailEntity.getId());
			
			HelpMenuEntity updateTailEntity = new HelpMenuEntity();
			updateTailEntity.setId(tailEntity.getId());
			updateTailEntity.setNextId(entity.getId());
			BaseEntityUtil.setModifyBaseEntityInfo(updateTailEntity, sysUser.getId(), sysUser.getName(), currentTime);
			updateById(updateTailEntity);
		}
		
		save(entity);
		
		return entity.getId();
	}
	
	private HelpMenuEntity getTail(String rootId, String helpVerId) {
		LambdaQueryWrapper<HelpMenuEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.isNull(HelpMenuEntity::getNextId)
			.eq(HelpMenuEntity::getHelpVerId, helpVerId)
			.eq(HelpMenuEntity::getDeleteStatus, HelpMenuEntity.DELETE_STATUS_NO);
		
		if(StringUtil.isEmpty(rootId))
			wrapper.isNull(HelpMenuEntity::getRootId);
		else
			wrapper.eq(HelpMenuEntity::getRootId, rootId);
			
		return baseMapper.selectOne(wrapper);
	}
	
	public HelpMenuEntity getHeader(String rootId, String helpVerId) {
		LambdaQueryWrapper<HelpMenuEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.isNull(HelpMenuEntity::getPrevId)
			.eq(HelpMenuEntity::getHelpVerId, helpVerId)
			.eq(HelpMenuEntity::getDeleteStatus, HelpMenuEntity.DELETE_STATUS_NO);
		
		if(StringUtil.isEmpty(rootId))
			wrapper.isNull(HelpMenuEntity::getRootId);
		else
			wrapper.eq(HelpMenuEntity::getRootId, rootId);
			
		return baseMapper.selectOne(wrapper);
	}
	
	public void changePoint(String id, String prevId, String nextId) {
		LambdaUpdateWrapper<HelpMenuEntity> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.set(HelpMenuEntity::getPrevId, prevId)
			.set(HelpMenuEntity::getNextId, nextId)
			.eq(HelpMenuEntity::getId, id);
		
		this.update(updateWrapper);
	}
	
	public void changeRootId(String id, String rootId) {
		LambdaUpdateWrapper<HelpMenuEntity> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.set(HelpMenuEntity::getRootId, rootId)
			.eq(HelpMenuEntity::getId, id);
		
		this.update(updateWrapper);
	}
	
	public void changePrevPoint(String id, String prevId) {
		LambdaUpdateWrapper<HelpMenuEntity> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.set(HelpMenuEntity::getPrevId, prevId)
			.eq(HelpMenuEntity::getId, id);
		
		this.update(updateWrapper);
	}
	
	public void changeNextPoint(String id, String nextId) {
		LambdaUpdateWrapper<HelpMenuEntity> updateWrapper = Wrappers.lambdaUpdate();
		updateWrapper.set(HelpMenuEntity::getNextId, nextId)
			.eq(HelpMenuEntity::getId, id);
		
		this.update(updateWrapper);
	}

	public PageOut<HelpMenuEntity> search(HelpMenuSearchIn in) {
		
		HelpVerEntity verEntity = verService.getByHelpIdAndVer(in.getHelpId(), in.getHelpVer());
		if(verEntity == null)
			throw BEUtil.failNormal("帮助版本不存在");
		
		LambdaQueryWrapper<HelpMenuEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HelpMenuEntity::getHelpVerId, verEntity.getId())
			.eq(HelpMenuEntity::getDeleteStatus, HelpMenuEntity.DELETE_STATUS_NO);
		
		if(StringUtil.isNotEmpty(in.getName()))
			wrapper.like(HelpMenuEntity::getName, in.getName());
		
		Integer totalCount = baseMapper.selectCount(wrapper);
		
		wrapper.last("limit " + in.getOffset() + ", " + in.getPagesize());
		List<HelpMenuEntity> items = baseMapper.selectList(wrapper);
		
		return new PageOut<HelpMenuEntity>(items, totalCount);
	}
	
	public void deleteByHelpVerId(String helpVerId, SysUserDto sysUser, Date currentTime) {
		HelpMenuEntity updateEntity = new HelpMenuEntity();
		updateEntity.setDeleteStatus(HelpMenuEntity.DELETE_STATUS_YES);
		BaseEntityUtil.setModifyBaseEntityInfo(updateEntity, sysUser.getId(), sysUser.getName(), currentTime);
		
		baseMapper.update(updateEntity
				, Wrappers.<HelpMenuEntity>lambdaUpdate()
					.eq(HelpMenuEntity::getHelpVerId, helpVerId));
	}
	
}
