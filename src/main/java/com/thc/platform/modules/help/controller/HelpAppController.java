package com.thc.platform.modules.help.controller;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.dto.HelpFeedbackIn;
import com.thc.platform.modules.help.dto.HelpMenuSearchIn;
import com.thc.platform.modules.help.dto.HelpMenuSearchOut;
import com.thc.platform.modules.help.dto.HelpMenuTreeOut;
import com.thc.platform.modules.help.dto.HelpPageEditOut;
import com.thc.platform.modules.help.dto.HelpVideoOut;
import com.thc.platform.modules.help.entity.HelpMenuEntity;
import com.thc.platform.modules.help.entity.HelpPageEntity;
import com.thc.platform.modules.help.entity.HelpPageVideoRelEntity;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.entity.HelpVideoEntity;
import com.thc.platform.modules.help.service.HelpFeedbackService;
import com.thc.platform.modules.help.service.HelpMenuService;
import com.thc.platform.modules.help.service.HelpPageService;
import com.thc.platform.modules.help.service.HelpPageVideoRelService;
import com.thc.platform.modules.help.service.HelpVerService;
import com.thc.platform.modules.help.service.HelpVideoService;

/**
 * 帮助Widget相关接口
 */
@RestController
@RequestMapping("/help/app")
public class HelpAppController {
	
	@Autowired
	private HelpMenuService helpMenuService;
	@Autowired
	private HelpFeedbackService helpFeedbackService;
	@Autowired
	private HelpPageService helpPageService;
	@Autowired
	private HelpVideoService helpVideoService;
	@Autowired
	private HelpPageVideoRelService helpPageVideoRelService;
	@Autowired
	private HelpVerService helpVerService;
	
	@GetMapping("/menu/getTree")
	public Api<HelpMenuTreeOut> getTree(@RequestParam("helpId") String helpId
			, @RequestParam(value = "ver", required = false) String ver) {
		
		if(StringUtil.isEmpty(helpId))
			throw BEUtil.failNormal("helpId is null or empty");
		
		if(StringUtil.isEmpty(ver))
			ver = HelpVerEntity.DEFAULT_VER;
		
		HelpMenuTreeOut out = helpMenuService.loadMenuTree(helpId, ver);
		
		return Api.ok(out);
	}
	
	@PostMapping("/content/search")
	public Api<List<HelpMenuSearchOut>> search(@RequestBody HelpMenuSearchIn in) {
		in.validate();
		
		PageOut<HelpMenuEntity> pageOut = helpMenuService.search(in);
		List<HelpMenuSearchOut> outs = new ArrayList<HelpMenuSearchOut>();
		for(HelpMenuEntity entity : pageOut.getItems()) 
			outs.add(new HelpMenuSearchOut(entity.getId(), entity.getName(), entity.getModifyTime()));
		
		return Api.ok(outs, pageOut.getTotalCount());
	}
	
	@GetMapping("/content/get")
	public Api<HelpPageEditOut> get(@RequestParam("menuId") String menuId) {
		HelpMenuEntity menuEntity = helpMenuService.getById(menuId);
		if(menuEntity == null)
			throw BEUtil.failNormal("菜单节点不存在");
		
		String pageId = menuEntity.getPageId();
		
		if(StringUtil.isEmpty(pageId))
			Api.ok(null);
		
		HelpPageEditOut out = new HelpPageEditOut();
		out.setMenuId(menuId);
		out.setMenuName(menuEntity.getName());
		HelpPageEntity pageEntity = helpPageService.getById(pageId);
		if(pageEntity != null) {
			out.setRichText(pageEntity.getRichText());
			out.setModifyTime(pageEntity.getModifyTime());
			
			List<HelpPageEditOut.VideoOut> videoOuts = new ArrayList<>();
			List<HelpPageVideoRelEntity> rels = helpPageVideoRelService.getByPageId(pageEntity.getId());
			for(HelpPageVideoRelEntity rel : rels) {
				HelpVideoEntity videoEntity = helpVideoService.getById(rel.getVideoId());
				if(videoEntity == null || videoEntity.getDeleteStatus() == HelpVideoEntity.DELETE_STATUS_YES)
					continue;
				
				HelpPageEditOut.VideoOut videoOut = new HelpPageEditOut.VideoOut();
				videoOut.setId(videoEntity.getId());
				videoOut.setName(videoEntity.getName());
				videoOut.setVideoUrl(videoEntity.getVideoUrl());
				videoOut.setVideoThumbnailUrl(videoEntity.getVideoThumbnailUrl());
				videoOut.setVideoCaptionFileUrl(videoEntity.getVideoCaptionFileUrl());
				videoOut.setBriefIntroduction(videoEntity.getBriefIntroduction());
				
				videoOuts.add(videoOut);
			}
			out.setVideos(videoOuts);
		}
		return Api.ok(out);
	}
	
	@PostMapping("/feedback/add")
	public Api<Object> feedback(@RequestBody HelpFeedbackIn in) {
		in.validate();
		helpFeedbackService.add(in);
		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/video/getById")
	public Api<Object> getById(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		HelpVideoEntity entity = helpVideoService.getById(id);
		if(entity == null)
			return Api.ok(null);
		
		HelpVerEntity verEntity = helpVerService.getById(entity.getHelpVerId());
		
		HelpVideoOut out = new HelpVideoOut();
		out.setId(entity.getId());
		out.setName(entity.getName());
		out.setVideoUrl(entity.getVideoUrl());
		out.setVideoThumbnailUrl(entity.getVideoThumbnailUrl());
		out.setVideoCaptionFileUrl(entity.getVideoCaptionFileUrl());
		if(verEntity != null) {
			out.setHelpId(verEntity.getId());
			out.setHelpVer(verEntity.getVer());
		}
		out.setBriefIntroduction(entity.getBriefIntroduction());
		out.setModifyTime(entity.getModifyTime());
		out.setCreateTime(entity.getCreateTime());
		if(StringUtil.isNotEmpty(entity.getTags()))
			out.setTags(Arrays.asList(entity.getTags().split(",")));
		
		return Api.ok(out);
	}
	
}
