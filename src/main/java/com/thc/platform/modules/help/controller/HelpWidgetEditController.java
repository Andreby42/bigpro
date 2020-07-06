package com.thc.platform.modules.help.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.help.dto.HelpWidgetBlockChangeOrderIn;
import com.thc.platform.modules.help.dto.HelpWidgetBlockOut;
import com.thc.platform.modules.help.dto.HelpWidgetBlockSaveIn;
import com.thc.platform.modules.help.dto.HelpWidgetOut;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.service.HelpWidgetBlockService;
import com.thc.platform.modules.help.service.HelpWidgetEditService;

/**
 * 帮助Widget相关接口
 */
@RestController
@RequestMapping("/help/admin/widget/edit")
public class HelpWidgetEditController {
	
	@Autowired
	private HelpWidgetEditService helpWidgetEditService;
	@Autowired
	private HelpWidgetBlockService helpWidgetBlockService;
	
	@PostMapping("/block/save")
	public Api<String> save(@RequestBody HelpWidgetBlockSaveIn in) {
		in.validate();
		String helpWidgetBlockId = helpWidgetEditService.saveWidgetBlock(in);
		return Api.ok(helpWidgetBlockId);
	}
	
	@PostMapping("/block/changeOrder")
	public Api<Object> changeOrder(@RequestBody HelpWidgetBlockChangeOrderIn in) {
		in.validate();
		helpWidgetBlockService.changeOrder(in);
		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/get")
	public Api<HelpWidgetOut> get(@RequestParam("helpId") String helpId
			, @RequestParam(value = "helpVer", required = false) String helpVer
			, @RequestParam("menuId") String menuId) {
		
		if(StringUtil.isEmpty(helpVer))
			helpVer = HelpVerEntity.DEFAULT_VER;
		
		HelpWidgetOut out = helpWidgetEditService.get(helpId, helpVer, menuId);
		
		return Api.ok(out);
	}
	
	@GetMapping("/block/get")
	public Api<HelpWidgetBlockOut> getWidgetBlock(@RequestParam("helpWidgetBlockId") String helpWidgetBlockId) {
		
		HelpWidgetBlockOut out = helpWidgetEditService.getWidgetBlock(helpWidgetBlockId);
		
		return Api.ok(out);
	}
	
	@GetMapping("/block/delete")
	public Api<Object> deleteWidgetBlock(@RequestParam("helpWidgetBlockId") String helpWidgetBlockId) {
		helpWidgetEditService.deleteWidgetBlock(helpWidgetBlockId);
		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/delete")
	public Api<Object> delete(@RequestParam("helpId") String helpId
			, @RequestParam(value = "helpVer", required = false) String helpVer
			, @RequestParam("menuId") String menuId) {
		
		helpWidgetEditService.deleteHelpWidget(helpId, helpVer, menuId);
		
		return Api.SUCCESS_RESULT;
	}
}
