package com.thc.platform.modules.help.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.dto.HelpMenuTreeOut;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.service.HelpMenuService;

/**
 * 帮助版本
 */
@RestController
@RequestMapping("/help/admin/menu")
public class HelpMenuController {

	@Autowired
	private HelpMenuService helpMenuService;
	
	@GetMapping("/getTree")
	public Api<HelpMenuTreeOut> getTree(@RequestParam("helpId") String helpId
			, @RequestParam("ver") String ver) {
		
		if(StringUtil.isEmpty(helpId))
			throw BEUtil.failNormal("helpId is null or empty");
		
		if(StringUtil.isEmpty(ver))
			ver = HelpVerEntity.DEFAULT_VER;
		
		HelpMenuTreeOut out = helpMenuService.loadMenuTree(helpId, ver);
		
		return Api.ok(out);
	}
	
}
