package com.thc.platform.modules.help.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.help.dto.HelpVerListIn;
import com.thc.platform.modules.help.dto.HelpVerOut;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.service.HelpVerService;

/**
 * 帮助版本
 */
@RestController
@RequestMapping("/help/admin/ver/")
public class HelpVerController {

	@Autowired
	private HelpVerService helpVerService;
	
	@PostMapping("/list")
	public Api<List<HelpVerOut>> list(@RequestBody HelpVerListIn in) {
		in.validate();
		
		PageOut<HelpVerEntity> pageOut = helpVerService.listPageByHelpId(in.getHelpId(), in.getOffset(), in.getPagesize());
		List<HelpVerOut> outs = new ArrayList<>();
		for(HelpVerEntity entity : pageOut.getItems())
			outs.add(new HelpVerOut(entity.getId(), entity.getVer()));
		
		return Api.ok(outs, pageOut.getTotalCount());
	}
	
}
