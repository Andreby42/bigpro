package com.thc.platform.modules.help.controller;

import java.util.ArrayList;
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
import com.thc.platform.modules.help.dto.HelpCopyIn;
import com.thc.platform.modules.help.dto.HelpDeleteIn;
import com.thc.platform.modules.help.dto.HelpEmptyIn;
import com.thc.platform.modules.help.dto.HelpMenuSearchIn;
import com.thc.platform.modules.help.dto.HelpMenuSearchOut;
import com.thc.platform.modules.help.dto.HelpMoveIn;
import com.thc.platform.modules.help.dto.HelpPageEditOut;
import com.thc.platform.modules.help.dto.HelpSaveIn;
import com.thc.platform.modules.help.dto.HelpVerSaveIn;
import com.thc.platform.modules.help.entity.HelpMenuEntity;
import com.thc.platform.modules.help.entity.HelpPageEntity;
import com.thc.platform.modules.help.entity.HelpPageVideoRelEntity;
import com.thc.platform.modules.help.entity.HelpVideoEntity;
import com.thc.platform.modules.help.service.HelpEditService;
import com.thc.platform.modules.help.service.HelpMenuService;
import com.thc.platform.modules.help.service.HelpPageService;
import com.thc.platform.modules.help.service.HelpPageVideoRelService;
import com.thc.platform.modules.help.service.HelpVideoService;

/**
 * 帮助编辑相关接口
 */
@RestController
@RequestMapping("/help/admin/edit")
public class HelpEditController {

	@Autowired
	private HelpEditService helpEditService;
	@Autowired
	private HelpPageService helpPageService;
	@Autowired
	private HelpMenuService helpMenuService;
	@Autowired
	private HelpPageVideoRelService helpPageVideoRelService;
	@Autowired
	private HelpVideoService helpVideoService;
	
	@PostMapping("/save")
	public Api<String> save(@RequestBody HelpSaveIn in) {
		in.validate();
		String menuId = helpEditService.save(in);
		return Api.ok(menuId);
	}

	@PostMapping("/move")
	public Api<Object> move(@RequestBody HelpMoveIn in) {
		in.validate();
		helpEditService.move(in);
		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/delete")
	public Api<Object> deleteHelpMenu(@RequestParam("menuId") String menuId) {
		if(StringUtil.isNotEmpty(menuId)) 
			helpEditService.deleteHelp(menuId);
		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/get")
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
			out.setModifyTime(menuEntity.getModifyTime());
			
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
	
	@PostMapping("/search")
	public Api<List<HelpMenuSearchOut>> search(@RequestBody HelpMenuSearchIn in) {
		in.validate();
		
		PageOut<HelpMenuEntity> pageOut = helpMenuService.search(in);
		List<HelpMenuSearchOut> outs = new ArrayList<HelpMenuSearchOut>();
		for(HelpMenuEntity entity : pageOut.getItems()) 
			outs.add(new HelpMenuSearchOut(entity.getId(), entity.getName(), entity.getModifyTime()));
		
		return Api.ok(outs, pageOut.getTotalCount());
	}
	
	@PostMapping("/ver/add")
	public Api<Object> addVer(@RequestBody HelpVerSaveIn in) {
		helpEditService.addVer(in);
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/ver/copy")
	public Api<Object> copy(@RequestBody HelpCopyIn in) {
		helpEditService.copy(in);
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/ver/empty")
	public Api<Object> emptyHelpVer(@RequestBody HelpEmptyIn in) {
		helpEditService.emptyHelpVer(in);
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/ver/delete")
	public Api<Object> deleteHelpVer(@RequestBody HelpDeleteIn in) {
		helpEditService.deleteHelpVer(in);
		return Api.SUCCESS_RESULT;
	}
	
}
