package com.thc.platform.modules.help.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gexin.fastjson.JSON;
import com.thc.platform.common.cmpt.DistributedLockCmpt;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.help.dto.HelpVideoOut;
import com.thc.platform.modules.help.dto.HelpWidgetBlockOut;
import com.thc.platform.modules.help.dto.HelpWidgetBlockSaveIn;
import com.thc.platform.modules.help.dto.HelpWidgetOut;
import com.thc.platform.modules.help.entity.HelpMenuEntity;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.entity.HelpVideoEntity;
import com.thc.platform.modules.help.entity.HelpWidgetBlockEntity;
import com.thc.platform.modules.help.entity.HelpWidgetEntity;
import com.thc.platform.modules.help.vo.HelpWidgetBlockDataVo;

@Service
public class HelpWidgetEditService {

	public static final String HELP_WIDGET_BLOCK_POINTER_LOCK_PREFIX = "global-platform-extend.helpWidgetBlock.pointer.lcok.";
	
	@Autowired
	private GlobalPlatformService globalPlatformService;
	@Autowired
	private HelpWidgetService widgetService;
	@Autowired
	private HelpWidgetBlockService widgetBlockService;
	@Autowired
	private HelpVerService verService;
	@Autowired
	private HelpMenuService helpMenuService;
	@Autowired
	private HelpVideoService helpVideoService;
	@Autowired
	private DistributedLockCmpt distributedLockCmpt;
	
	@Transactional
	public String saveWidgetBlock(HelpWidgetBlockSaveIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		Date currentTime = new Date();
		if(StringUtil.isEmpty(in.getHelpWidgetBlockId()))
			return add(in, currentTime, sysUser);
		else
			return update(in, currentTime, sysUser);
	}
	
	private String add(HelpWidgetBlockSaveIn in, Date currentTime, SysUserDto sysUser) {
		HelpVerEntity verEntity = verService.getByHelpIdAndVer(in.getHelpId(), in.getHelpVer());
		if(verEntity == null)
			throw BEUtil.failNormal("帮助版本不存在");
		
		String helpVerId = verEntity.getId();
		HelpWidgetEntity widgetEntity = widgetService.getByHelpVerIdAndMenuId(helpVerId, in.getMenuId());
		if(widgetEntity == null)
			widgetEntity = widgetService.add(helpVerId, in.getMenuId(), sysUser, currentTime);
		
		String lockKey = HELP_WIDGET_BLOCK_POINTER_LOCK_PREFIX + widgetEntity.getId();
		boolean locked = distributedLockCmpt.lock(lockKey, 1, TimeUnit.MINUTES, 5, 1, TimeUnit.SECONDS);
		if(locked) {
			String data = getWidgetBlockData(in);
			HelpWidgetBlockEntity entity = widgetBlockService.add(widgetEntity.getId(), helpVerId, in.getType(), data, sysUser, currentTime);
			distributedLockCmpt.release(lockKey);
			return entity.getId();
		} else {
			throw BEUtil.failNormal("修改冲突，请稍后再试");
		}
	}
	
	private String update(HelpWidgetBlockSaveIn in, Date currentTime, SysUserDto sysUser) {
		String data = getWidgetBlockData(in);
		widgetBlockService.updateDataById(in.getHelpWidgetBlockId(), data, sysUser, currentTime);
		return in.getHelpWidgetBlockId();
	}
	
	private String getWidgetBlockData(HelpWidgetBlockSaveIn in) {
		Map<String, Object> dataMap = new HashMap<>();
		if(in.getType() == HelpWidgetBlockEntity.TYPE_ONE_RICH_TEXT) {
			dataMap.put("helpMenuId", in.getHelpMenuId());
			dataMap.put("helpNotes", in.getHelpNotes());
		} else if(in.getType() == HelpWidgetBlockEntity.TYPE_MANY_RICH_TEXT) {
			dataMap.put("helpMenuIds", in.getHelpMenuIds());
		} else if(in.getType() == HelpWidgetBlockEntity.TYPE_VIDEO) {
			dataMap.put("videoId", in.getVideoId());
		}
		return JSON.toJSONString(dataMap);
	}

	public HelpWidgetOut get(String helpId, String helpVer, String menuId) {
		HelpVerEntity verEntity = verService.getByHelpIdAndVer(helpId, helpVer);
		if(verEntity == null)
			throw BEUtil.failNormal("帮助版本不存在");
		
		HelpWidgetEntity widgetEntity = widgetService.getByHelpVerIdAndMenuId(verEntity.getId(), menuId);
		if(widgetEntity == null) 
			return null;
		
		HelpWidgetOut out = new HelpWidgetOut();
		out.setMenuId(menuId);
		
		List<HelpWidgetOut.WidgetBlockOut> blockOuts = new ArrayList<>();
		out.setBlocks(blockOuts);
		
		List<HelpWidgetBlockEntity> blockEntities = widgetBlockService.getByWidgetId(widgetEntity.getId());
		for(HelpWidgetBlockEntity blockEntity : blockEntities) {
			int type = blockEntity.getType();
			
			HelpWidgetOut.WidgetBlockOut wbOut = new HelpWidgetOut.WidgetBlockOut();
			wbOut.setId(blockEntity.getId());
			wbOut.setType(type);
			
			HelpWidgetBlockDataVo dataVo = JSON.parseObject(blockEntity.getData(), HelpWidgetBlockDataVo.class);
			if(type == HelpWidgetBlockEntity.TYPE_ONE_RICH_TEXT) {
				HelpWidgetOut.HelpMenuOut helpMenu = getHelpMenuOut(dataVo.getHelpMenuId());
				// 帮助内容已被删除情况
				if(helpMenu == null)
					continue;
				
				wbOut.setHelpMenu(helpMenu);
				wbOut.setHelpNotes(dataVo.getHelpNotes());
			} else if(type == HelpWidgetBlockEntity.TYPE_MANY_RICH_TEXT) {
				List<HelpWidgetOut.HelpMenuOut> helpMenus = new ArrayList<>();
				for(String helpMenuId : dataVo.getHelpMenuIds()) {
					HelpWidgetOut.HelpMenuOut helpMenu = getHelpMenuOut(helpMenuId);
					// 帮助内容已被删除情况
					if(helpMenu == null)
						continue;
					
					helpMenus.add(helpMenu);
				}
				// 多条帮助内容 都被删除情况
				if(helpMenus.size() == 0)
					continue;
				
				wbOut.setHelpMenus(helpMenus);
			} else if(type == HelpWidgetBlockEntity.TYPE_VIDEO) {
				HelpVideoOut video = getHelpVideoOut(dataVo.getVideoId());
				// 帮助视频已被删除情况
				if(video == null)
					continue;
				
				wbOut.setVideo(video);
			}
			
			blockOuts.add(wbOut);
		}
		return out;
	}
	
	private HelpWidgetOut.HelpMenuOut getHelpMenuOut(String helpMenuId) {
		HelpMenuEntity entity = helpMenuService.getById(helpMenuId);
		
		if(entity == null || entity.getDeleteStatus() == HelpMenuEntity.DELETE_STATUS_YES)
			return null;
		
		HelpWidgetOut.HelpMenuOut helpMenu = new HelpWidgetOut.HelpMenuOut();
		helpMenu.setHelpMenuId(helpMenuId);
		helpMenu.setHelpMenuName(entity.getName());
		
		return helpMenu;
	}
	
	private HelpVideoOut getHelpVideoOut(String videoId) {
		HelpVideoEntity entity = helpVideoService.getById(videoId);
		if(entity == null || entity.getDeleteStatus() == HelpVideoEntity.DELETE_STATUS_YES)
			return null;
		
		HelpVideoOut out = new HelpVideoOut();
		out.setId(entity.getId());
		out.setName(entity.getName());
		out.setVideoUrl(entity.getVideoUrl());
		out.setVideoThumbnailUrl(entity.getVideoThumbnailUrl());
		out.setVideoCaptionFileUrl(entity.getVideoCaptionFileUrl());
		out.setBriefIntroduction(entity.getBriefIntroduction());
		
		return out;
	}
	
	public HelpWidgetBlockOut getWidgetBlock(String id) {
		HelpWidgetBlockEntity entity = widgetBlockService.getById(id);
		int type = entity.getType();
		
		HelpWidgetBlockOut out = new HelpWidgetBlockOut();
		out.setId(entity.getId());
		out.setType(type);
		
		HelpWidgetBlockDataVo dataVo = JSON.parseObject(entity.getData(), HelpWidgetBlockDataVo.class);
		if(type == HelpWidgetBlockEntity.TYPE_ONE_RICH_TEXT) {
			HelpMenuEntity menuEntity = helpMenuService.getById(dataVo.getHelpMenuId());
			if(menuEntity == null || menuEntity.getDeleteStatus() == HelpMenuEntity.DELETE_STATUS_YES)
				throw BEUtil.failNormal("帮助内容已删除，请刷新帮助Widget");
			
			HelpWidgetBlockOut.HelpMenuOut helpMenu = new HelpWidgetBlockOut.HelpMenuOut();
			helpMenu.setHelpMenuId(dataVo.getHelpMenuId());
			helpMenu.setHelpMenuName(menuEntity.getName());
			
			out.setHelpMenu(helpMenu);
			out.setHelpNotes(dataVo.getHelpNotes());
		} else if(type == HelpWidgetBlockEntity.TYPE_MANY_RICH_TEXT) {
			List<HelpWidgetBlockOut.HelpMenuOut> helpMenus = new ArrayList<>();
			for(String helpMenuId : dataVo.getHelpMenuIds()) {
				HelpMenuEntity menuEntity = helpMenuService.getById(helpMenuId);
				if(menuEntity == null || menuEntity.getDeleteStatus() == HelpMenuEntity.DELETE_STATUS_YES)
					continue;
				
				HelpWidgetBlockOut.HelpMenuOut helpMenu = new HelpWidgetBlockOut.HelpMenuOut();
				helpMenu.setHelpMenuId(helpMenuId);
				helpMenu.setHelpMenuName(menuEntity.getName());
				
				helpMenus.add(helpMenu);
			}
			if(helpMenus.size() == 0)
				throw BEUtil.failNormal("帮助内容已删除，请刷新帮助Widget");
			
			out.setHelpMenus(helpMenus);
		} else if(type == HelpWidgetBlockEntity.TYPE_VIDEO) {
			HelpVideoOut video = getHelpVideoOut(dataVo.getVideoId());
			if(video == null)
				throw BEUtil.failNormal("帮助视频已删除，请刷新帮助Widget");
			
			out.setVideo(video);
		}
		return out;
	}
	
	@Transactional
	public void deleteHelpWidget(String helpId, String helpVer, String menuId) {
		HelpVerEntity verEntity = verService.getByHelpIdAndVer(helpId, helpVer);
		if(verEntity == null)
			throw BEUtil.failNormal("帮助版本不存在");
		
		HelpWidgetEntity widgetEntity = widgetService.getByHelpVerIdAndMenuId(verEntity.getId(), menuId);
		if(widgetEntity == null) 
			return;
		
		widgetBlockService.deleteByWidgetId(widgetEntity.getId());
		widgetService.removeById(widgetEntity.getId());
	}
	
	public void deleteWidgetBlock(String helpWidgetBlockId) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		HelpWidgetBlockEntity entity = widgetBlockService.getById(helpWidgetBlockId);
		if(entity == null)
			return;
		
		String lockKey = HELP_WIDGET_BLOCK_POINTER_LOCK_PREFIX + entity.getWidgetId();
		boolean locked = distributedLockCmpt.lock(lockKey, 1, TimeUnit.MINUTES, 5, 1, TimeUnit.SECONDS);
		if(locked) {
			Date currentTime = new Date();
			
			String prevId = entity.getPrevId();
			if(StringUtil.isNotEmpty(prevId)) 
				widgetBlockService.updateNextId(prevId, entity.getNextId(), sysUser, currentTime);
			
			String nextId = entity.getNextId();
			if(StringUtil.isNotEmpty(nextId)) 
				widgetBlockService.updatePrevId(nextId, entity.getPrevId(), sysUser, currentTime);
			
			widgetBlockService.removeById(helpWidgetBlockId);
			distributedLockCmpt.release(lockKey);
		} else {
			throw BEUtil.failNormal("修改冲突，请稍后再试");
		}
	}
}
