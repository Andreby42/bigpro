package com.thc.platform.modules.help.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.dto.HelpWidgetMenuTreeOut;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.service.HelpWidgetMenuService;

/**
 * 帮助版本
 */
@RestController
@RequestMapping("/help/admin/widget-menu")
public class HelpWidgetMenuController {

	@Autowired
	private HelpWidgetMenuService helpWidgetMenuService;
	
	@GetMapping("/getTree")
	public Api<HelpWidgetMenuTreeOut> getTree(@RequestParam("helpId") String helpId
			, @RequestParam(value = "ver", required = false) String ver) {
		
		if(StringUtil.isEmpty(helpId))
			throw BEUtil.failNormal("helpId is null or empty");
		
		if(StringUtil.isEmpty(ver))
			ver = HelpVerEntity.DEFAULT_VER;
		
		HelpWidgetMenuTreeOut out = helpWidgetMenuService.loadMenuTree(helpId, ver);
		
		return Api.ok(out);
	}
	
}
