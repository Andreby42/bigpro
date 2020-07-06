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
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.dto.HelpVideoListIn;
import com.thc.platform.modules.help.dto.HelpVideoOut;
import com.thc.platform.modules.help.dto.HelpVideoSaveIn;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.entity.HelpVideoEntity;
import com.thc.platform.modules.help.service.HelpVerService;
import com.thc.platform.modules.help.service.HelpVideoService;

/**
 * 帮助视频相关接口
 */
@RestController
@RequestMapping("/help/admin/video")
public class HelpVideoController {

	@Autowired
	private HelpVideoService helpVideoService;
	@Autowired
	private HelpVerService helpVerService;
	
	@PostMapping("/save")
	public Api<Object> save(@RequestBody HelpVideoSaveIn in) {
		in.validate();
		
		helpVideoService.save(in);
		
		return Api.SUCCESS_RESULT;
	}

	@PostMapping("/list")
	public Api<List<HelpVideoOut>> list(@RequestBody HelpVideoListIn in) {
		in.validate();
		
		PageOut<HelpVideoEntity> pageOut = helpVideoService.listPage(in);
		List<HelpVideoOut> outs = new ArrayList<>();
		for(HelpVideoEntity entity : pageOut.getItems()) {
			HelpVideoOut out = new HelpVideoOut();
			out.setId(entity.getId());
			out.setName(entity.getName());
			out.setVideoUrl(entity.getVideoUrl());
			out.setVideoThumbnailUrl(entity.getVideoThumbnailUrl());
			out.setVideoCaptionFileUrl(entity.getVideoCaptionFileUrl());
			
			HelpVerEntity verEntity = helpVerService.getById(entity.getHelpVerId());
			if(verEntity != null) {
				out.setHelpId(verEntity.getHelpId());
				out.setHelpVer(verEntity.getVer());
			}
			out.setBriefIntroduction(entity.getBriefIntroduction());
			out.setCreateTime(entity.getCreateTime());
			if(StringUtil.isNotEmpty(entity.getTags()))
				out.setTags(Arrays.asList(entity.getTags().split(",")));
			
			outs.add(out);
		}
		return Api.ok(outs, pageOut.getTotalCount());
	}
	
	@GetMapping("/delete")
	public Api<Object> delete(@RequestParam("id") String id) {
		if(StringUtil.isNotEmpty(id)) 
			helpVideoService.updateDeleteStatus(id);
		
		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/getById")
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
		out.setCreateTime(entity.getCreateTime());
		if(StringUtil.isNotEmpty(entity.getTags()))
			out.setTags(Arrays.asList(entity.getTags().split(",")));
		
		return Api.ok(out);
	}
}
