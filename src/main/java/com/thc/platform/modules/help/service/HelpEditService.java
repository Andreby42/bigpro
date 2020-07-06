package com.thc.platform.modules.help.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thc.platform.common.cmpt.DistributedLockCmpt;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.BaseEntityUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.help.dto.HelpCopyIn;
import com.thc.platform.modules.help.dto.HelpDeleteIn;
import com.thc.platform.modules.help.dto.HelpEmptyIn;
import com.thc.platform.modules.help.dto.HelpMoveIn;
import com.thc.platform.modules.help.dto.HelpSaveIn;
import com.thc.platform.modules.help.dto.HelpVerSaveIn;
import com.thc.platform.modules.help.entity.HelpMenuEntity;
import com.thc.platform.modules.help.entity.HelpPageEntity;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.entity.HelpWidgetBlockEntity;
import com.thc.platform.modules.help.entity.HelpWidgetEntity;
import com.thc.platform.modules.help.entity.HelpWidgetMenuEntity;

@Service
public class HelpEditService {

	private static final String HELP_MENU_POINTER_LOCK_PREFIX = "global-platform-extend.helpMenu.pointer.lcok.";
	
	@Autowired
	private GlobalPlatformService globalPlatformService;
	@Autowired
	private HelpPageVideoRelService pageVideoRelService;
	@Autowired
	private HelpPageService pageService;
	@Autowired
	private HelpMenuService menuService;
	@Autowired
	private HelpVerService verService;
	@Autowired
	private DistributedLockCmpt distributedLockCmpt;
	@Autowired
	private HelpWidgetMenuService widgetMenuService;
	@Autowired
	private HelpWidgetService widgetService;
	@Autowired
	private HelpWidgetBlockService widgetBlockService;
	
	@Transactional
	public String save(HelpSaveIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		Date currentTime = new Date();
		if(StringUtil.isEmpty(in.getMenuId())) {
			if(StringUtil.isEmpty(in.getRootMenuId()))
				return addRoot(in, sysUser, currentTime);
			else
				return add(in, sysUser, currentTime);
		} else {
			update(in, currentTime, sysUser);
			return in.getMenuId();
		}
	}
	
	private String addRoot(HelpSaveIn in, SysUserDto sysUser, Date currentTime) {
		HelpVerEntity verEntity = verService.getByHelpIdAndVer(in.getHelpId(), in.getHelpVer());
		if(verEntity == null)
			throw BEUtil.failNormal("帮助版本不存在");
		
		String helpMenuId = null;
		String lockKey = HELP_MENU_POINTER_LOCK_PREFIX + verEntity.getId();
		boolean locked = distributedLockCmpt.lock(lockKey, 1, TimeUnit.MINUTES, 5, 1, TimeUnit.SECONDS);
		if(locked) {
			HelpPageEntity pageEntity = pageService.add(in.getRichText(), verEntity.getId(), sysUser, currentTime);
			pageVideoRelService.batchAdd(in.getHelpVideoIds(), pageEntity.getId(), currentTime, sysUser);
			helpMenuId = menuService.add(in, verEntity.getId(), pageEntity.getId(), sysUser, currentTime);
			distributedLockCmpt.release(lockKey);
		} else {
			throw BEUtil.failNormal("修改冲突，请稍后再试");
		}
		return helpMenuId;
	}
	
	private String add(HelpSaveIn in, SysUserDto sysUser, Date currentTime) {
		HelpMenuEntity rootMenuEntity = menuService.getById(in.getRootMenuId());
		if(rootMenuEntity == null)
			throw BEUtil.failNormal("上级菜单不存在");
		
		String helpMenuId = null;
		String lockKey = HELP_MENU_POINTER_LOCK_PREFIX + rootMenuEntity.getHelpVerId();
		boolean locked = distributedLockCmpt.lock(lockKey, 1, TimeUnit.MINUTES, 5, 1, TimeUnit.SECONDS);
		if(locked) {
			HelpPageEntity pageEntity = pageService.add(in.getRichText(), rootMenuEntity.getHelpVerId(), sysUser, currentTime);
			pageVideoRelService.batchAdd(in.getHelpVideoIds(), pageEntity.getId(), currentTime, sysUser);
			
			helpMenuId =  menuService.add(in, rootMenuEntity.getHelpVerId(), pageEntity.getId(), sysUser, currentTime);
			distributedLockCmpt.release(lockKey);
		}  else {
			throw BEUtil.failNormal("修改冲突，请稍后再试");
		}
		return helpMenuId;
	}
	
	private void update(HelpSaveIn in, Date currentTime, SysUserDto sysUser) {
		HelpMenuEntity menuEntity = menuService.getById(in.getMenuId());
		if(menuEntity == null)
			throw BEUtil.failNormal("菜单不存在");
		
		String pageId = menuEntity.getPageId();
		HelpPageEntity pageEntity = pageService.getById(pageId);
		if(!pageEntity.getCreateHelpVerId().equals(menuEntity.getHelpVerId())) {
			pageEntity = pageService.add(in.getRichText(), menuEntity.getHelpVerId(), sysUser, currentTime);
		} else {
			int countRelMenu = menuService.countByPageId(pageId);
			if(countRelMenu > 1)
				pageEntity = pageService.add(in.getRichText(), menuEntity.getHelpVerId(), sysUser, currentTime);
			else
				pageService.update(pageId, in.getRichText(), sysUser, currentTime);
		}
		pageVideoRelService.batchAdd(in.getHelpVideoIds(), pageEntity.getId(), currentTime, sysUser);
		
		menuService.update(menuEntity.getId(), in.getMenuName(), pageEntity.getId(), sysUser, currentTime);
	}
	
	@Transactional
	public void move(HelpMoveIn in) {
		HelpMenuEntity entity = menuService.getById(in.getId());
		if(entity == null)
			throw BEUtil.failNormal("移动节点不存在，请刷新页面");
		
		String lockKey = HELP_MENU_POINTER_LOCK_PREFIX + entity.getHelpVerId();
		boolean locked = distributedLockCmpt.lock(lockKey, 1, TimeUnit.MINUTES, 5, 1, TimeUnit.SECONDS);
		if(locked) {
			// 更改移动节点上一个节点指针信息: nextId
			String prevId = entity.getPrevId();
			if(StringUtil.isNotEmpty(prevId))
				menuService.changeNextPoint(prevId, entity.getNextId());
			
			// 更改移动节点下一个节点指针信息: prevId
			String nextId = entity.getNextId();
			if(StringUtil.isNotEmpty(nextId))
				menuService.changePrevPoint(nextId, prevId);
			
			if(StringUtil.isNotEmpty(in.getAfterId()))
				moveAfter(in.getAfterId(), entity);
			else
				moveChild(in.getParentId(), entity);
			
			distributedLockCmpt.release(lockKey);
		}  else {
			throw BEUtil.failNormal("移动冲突，请稍后再试");
		}
	}
	
	private void moveAfter(String afterId, HelpMenuEntity entity) {
		HelpMenuEntity afterEntity = menuService.getById(afterId);
		if(afterEntity == null)
			throw BEUtil.failNormal("参照节点不存在。afterId: " + afterId);
		
		// 更改移动节点指针信息
		menuService.changePoint(entity.getId(), afterId, afterEntity.getNextId());
		// 更改根节点信息，可能会改变
		menuService.changeRootId(entity.getId(), afterEntity.getRootId());
		// 更改after节点下一个节点指针信息
		if(StringUtil.isNotEmpty(afterEntity.getNextId()))
			menuService.changePrevPoint(afterEntity.getNextId(), entity.getId());
		// 更改after节点指针信息
		menuService.changeNextPoint(afterId, entity.getId());
	}
	
	private void moveChild(String parentId, HelpMenuEntity entity) {
		HelpMenuEntity headEntity = menuService.getHeader(parentId, entity.getHelpVerId());
		if(headEntity == null) {
			// 更改移动节点指针信息
			menuService.changePoint(entity.getId(), null, null);
		} else {
			// 更改移动节点next指针信息
			menuService.changePoint(entity.getId(), null, headEntity.getId());
			// 更改原头节点prev指针信息
			menuService.changePrevPoint(headEntity.getId(), entity.getId());
		}
		menuService.changeRootId(entity.getId(), parentId);
	}
	
	@Transactional
	public void deleteHelp(String id) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		HelpMenuEntity entity = menuService.getById(id);
		
		String lockKey = HELP_MENU_POINTER_LOCK_PREFIX + entity.getHelpVerId();
		boolean locked = distributedLockCmpt.lock(lockKey, 1, TimeUnit.MINUTES, 5, 1, TimeUnit.SECONDS);
		if(locked) {
			// 更改移动节点上一个节点指针信息
			String prevId = entity.getPrevId();
			if(StringUtil.isNotEmpty(prevId))
				menuService.changeNextPoint(prevId, entity.getNextId());
			
			// 更改移动节点下一个节点指针信息
			String nextId = entity.getNextId();
			if(StringUtil.isNotEmpty(nextId))
				menuService.changePrevPoint(nextId, prevId);
			
			Date currentTime = new Date();
			
			// 递归删除
			recursionDeleteHelp(entity, sysUser, currentTime);
			distributedLockCmpt.release(lockKey);
		} else {
			throw BEUtil.failNormal("修改冲突，请稍后再试");
		}
	}
	
	private void recursionDeleteHelp(HelpMenuEntity entity, SysUserDto sysUser, Date currentTime) {
		// 删除当前节点
		HelpMenuEntity updateEntity = new HelpMenuEntity();
		updateEntity.setId(entity.getId());
		updateEntity.setDeleteStatus(HelpMenuEntity.DELETE_STATUS_YES);
		BaseEntityUtil.setModifyBaseEntityInfo(updateEntity, sysUser.getId(), sysUser.getName(), currentTime);
		menuService.updateById(updateEntity);
		
		// 删除子节点
		List<HelpMenuEntity> children = menuService.getByHelpVerIdAndRootId(entity.getHelpVerId(), entity.getId());
		for(HelpMenuEntity child : children)
			recursionDeleteHelp(child, sysUser, currentTime);
	}
	
	@Transactional
	public void copy(HelpCopyIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		HelpVerEntity srcHelpVer = verService.getByHelpIdAndVer(in.getHelpId(), in.getSrcHelpVer());
		if(srcHelpVer == null)
			throw BEUtil.failNormal("复制源帮助版本不存在");
		
		HelpVerEntity targetHelpVer = verService.getByHelpIdAndVer(in.getHelpId(), in.getTargetHelpVer());
		if(targetHelpVer == null)
			throw BEUtil.failNormal("复制目标帮助版本不存在");
		
		Date currentTime = new Date();
		menuService.deleteByHelpVerId(targetHelpVer.getId(), sysUser, currentTime);
		
		List<HelpMenuEntity> targetAllList = new ArrayList<>();
		recursionCopy(srcHelpVer.getId(), targetHelpVer.getId()
				, null, null
				, sysUser, currentTime
				, targetAllList);
		
		menuService.saveBatch(targetAllList);
		
		// 清空 复制widget信息
		widgetBlockService.deleteByHelpVerId(targetHelpVer.getId());
		widgetService.deleteByHelpVerId(targetHelpVer.getId());
		recursionCopyWidgets(srcHelpVer.getId(), targetHelpVer.getId()
				, HelpWidgetMenuEntity.ROOT_ID
				, sysUser, currentTime);
	}
	
	private void recursionCopy(String srcHelpVerId, String targetHelpVerId
			, String srcRootId, String targetRootId
			, SysUserDto sysUser, Date currentTime
			, List<HelpMenuEntity> targetAllList) {
		
		List<HelpMenuEntity> srcRootMenuEntities = menuService.getByHelpVerIdAndRootId(srcHelpVerId, srcRootId);
		
		List<HelpMenuEntity> targetRootMenuEntities = new ArrayList<>(srcRootMenuEntities.size());
		for(int i = 0; i < srcRootMenuEntities.size(); i ++) {
			HelpMenuEntity src = srcRootMenuEntities.get(i);
			
			HelpMenuEntity target = new HelpMenuEntity();
			target.setId(ShortUUID.uuid());
			target.setRootId(targetRootId);
			target.setName(src.getName());
			target.setHelpVerId(targetHelpVerId);
			target.setPageId(src.getPageId());
			
			if(i > 0) {
				HelpMenuEntity targetPrev = targetRootMenuEntities.get(i - 1);
				target.setPrevId(targetPrev.getId());
				targetPrev.setNextId(target.getId());
			}
			BaseEntityUtil.setCreateAndModifyBaseEntityInfo(src, sysUser.getId(), sysUser.getName(), currentTime);
			targetRootMenuEntities.add(target);
			
			recursionCopy(srcHelpVerId, targetHelpVerId
					, src.getId(), target.getId()
					, sysUser, currentTime
					, targetAllList);
		}
		targetAllList.addAll(targetRootMenuEntities);
	}
	
	private void recursionCopyWidgets(String srcHelpVerId
			, String targetHelpVerId
			, String srcRootId
			, SysUserDto sysUser, Date currentTime) {
		
		List<HelpWidgetMenuEntity> srcWidgetMenuEntities = widgetMenuService.getByRootId(srcRootId, srcHelpVerId);
		for(HelpWidgetMenuEntity srcWidgetMenuEntity : srcWidgetMenuEntities) {
			// 递归copy子节点
			recursionCopyWidgets(srcHelpVerId, targetHelpVerId, srcWidgetMenuEntity.getMenuId(), sysUser, currentTime);
						
			HelpWidgetMenuEntity targetWidgetMenuEntity = widgetMenuService.getByMenuIdAndHelpVerId(srcWidgetMenuEntity.getMenuId(), targetHelpVerId);
			// 新版本菜单不存在，不复制
			if(targetWidgetMenuEntity == null)
				continue;
			
			// 源 HelpWidgetEntity 不存在不复制
			HelpWidgetEntity srcWidgetEntity = widgetService.getByHelpVerIdAndMenuId(srcHelpVerId, srcWidgetMenuEntity.getMenuId());
			if(srcWidgetEntity == null)
				continue;

			// 复制 HelpWidgetEntity 
			HelpWidgetEntity targetWidgetEntity = new HelpWidgetEntity();
			BeanUtils.copyProperties(srcWidgetEntity, targetWidgetEntity);
			targetWidgetEntity.setId(ShortUUID.uuid());
			targetWidgetEntity.setHelpVerId(targetHelpVerId);
			BaseEntityUtil.setCreateAndModifyBaseEntityInfo(targetWidgetEntity, sysUser.getId(), sysUser.getName(), currentTime);
			widgetService.save(targetWidgetEntity);
			
			// 源 HelpWidgetBlockEntity 集合不存在 不复制
			List<HelpWidgetBlockEntity> srcWidgetBlockEntities = widgetBlockService.getByWidgetId(srcWidgetEntity.getId());
			if(srcWidgetBlockEntities.size() == 0)
				continue;
			
			List<HelpWidgetBlockEntity> targetWidgetBlockEntities = new ArrayList<>();
			for(int i = 0; i < srcWidgetBlockEntities.size(); i ++) {
				HelpWidgetBlockEntity srcWidgetBlockEntity = srcWidgetBlockEntities.get(i);
				HelpWidgetBlockEntity targetWidgetBlockEntity = new HelpWidgetBlockEntity();
				BeanUtils.copyProperties(srcWidgetBlockEntity, targetWidgetBlockEntity);
				targetWidgetBlockEntity.setId(ShortUUID.uuid());
				targetWidgetBlockEntity.setWidgetId(targetWidgetEntity.getId());
				targetWidgetBlockEntity.setHelpVerId(targetHelpVerId);
				BaseEntityUtil.setCreateAndModifyBaseEntityInfo(targetWidgetBlockEntity, sysUser.getId(), sysUser.getName(), currentTime);
				if(i > 0) {
					HelpWidgetBlockEntity prevWidgetBlockEntity = targetWidgetBlockEntities.get(i - 1);
					targetWidgetBlockEntity.setPrevId(prevWidgetBlockEntity.getId());
					prevWidgetBlockEntity.setNextId(targetWidgetBlockEntity.getId());
				}
				targetWidgetBlockEntities.add(targetWidgetBlockEntity);
			}
			widgetBlockService.saveBatch(targetWidgetBlockEntities);
		}
	}
	
	@Transactional
	public void emptyHelpVer(HelpEmptyIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		HelpVerEntity helpVer = verService.getByHelpIdAndVer(in.getHelpId(), in.getHelpVer());
		if(helpVer == null)
			throw BEUtil.failNormal("清空帮助版本不存在");
		
		Date currentTime = new Date();
		
		menuService.deleteByHelpVerId(helpVer.getId(), sysUser, currentTime);
		// 清空 复制widget信息
		widgetBlockService.deleteByHelpVerId(helpVer.getId());
		widgetService.deleteByHelpVerId(helpVer.getId());
	}
	
	@Transactional
	public void deleteHelpVer(HelpDeleteIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		HelpVerEntity helpVer = verService.getByHelpIdAndVer(in.getHelpId(), in.getHelpVer());
		if(helpVer == null)
			throw BEUtil.failNormal("清空帮助版本不存在");
		
		Date currentTime = new Date();
		
		menuService.deleteByHelpVerId(helpVer.getId(), sysUser, currentTime);
		
		// 清空 复制widget信息
		widgetBlockService.deleteByHelpVerId(helpVer.getId());
		widgetService.deleteByHelpVerId(helpVer.getId());
		
		HelpVerEntity updateHelpVer = new HelpVerEntity();
		updateHelpVer.setId(helpVer.getId());
		updateHelpVer.setDeleteStatus(HelpVerEntity.DELETE_STATUS_YES);
		verService.updateById(updateHelpVer);
	}
	
	public void addVer(HelpVerSaveIn in) {
		HelpVerEntity verEntity = verService.getByHelpIdAndVer(in.getHelpId(), in.getHelpVer());
		if(verEntity != null)
			throw BEUtil.failNormal("帮助版本已存在");
		
		HelpVerEntity entity = new HelpVerEntity();
		
		entity.setId(ShortUUID.uuid());
		entity.setHelpId(in.getHelpId());
		entity.setVer(in.getHelpVer());
		
		verService.save(entity);
	}
	
}
