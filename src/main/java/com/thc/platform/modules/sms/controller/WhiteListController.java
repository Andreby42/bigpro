package com.thc.platform.modules.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.sms.dto.WhiteNumAddIn;
import com.thc.platform.modules.sms.dto.WhiteNumDeleteIn;
import com.thc.platform.modules.sms.dto.WhiteNumListIn;
import com.thc.platform.modules.sms.entity.WhiteNumEntity;
import com.thc.platform.modules.sms.service.WhiteNumService;

/**
 * 白名单接口类
 */
@RestController
@RequestMapping("/sms/white-list")
public class WhiteListController {

	@Autowired
	private WhiteNumService whiteNumService;
	
	@PostMapping("/add")
	public Api<Object> add(@RequestBody WhiteNumAddIn in) {
		in.validate();
		
		whiteNumService.add(in);
		
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/list")
	public Api<List<WhiteNumEntity>> list(@RequestBody WhiteNumListIn in) {
		in.validate();
		PageOut<WhiteNumEntity> pageOut = whiteNumService.list(in);
		
		return Api.ok(pageOut.getItems(), pageOut.getTotalCount());
	}

	@PostMapping("/delete")
	public Api<Object> delete(@RequestBody WhiteNumDeleteIn in) {
		in.validate();
		
		whiteNumService.deleteMobile(in.getYtxAppId(), in.getMobile());
		
		return Api.SUCCESS_RESULT;
	}
	
}
