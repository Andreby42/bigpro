package com.thc.platform.modules.help.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.dto.HelpWidgetOut;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.service.HelpWidgetEditService;

/**
 * 帮助Widget相关接口
 */
@RestController
@RequestMapping("/help/app/widget")
public class HelpWidgetAppController {
	
	@Autowired
	private HelpWidgetEditService helpWidgetEditService;
	
	@GetMapping("/get")
	public Api<HelpWidgetOut> get(@RequestParam("helpId") String helpId
			, @RequestParam(value = "helpVer", required = false) String helpVer
			, @RequestParam("menuId") String menuId) {
		
		if(StringUtil.isEmpty(helpVer))
			helpVer = HelpVerEntity.DEFAULT_VER;
		
		HelpWidgetOut out = helpWidgetEditService.get(helpId, helpVer, menuId);
		
		return Api.ok(out);
	}
	
}
