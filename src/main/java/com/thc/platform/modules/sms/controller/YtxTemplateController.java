package com.thc.platform.modules.sms.controller;

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
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.sms.dto.YtxTemplateListIn;
import com.thc.platform.modules.sms.dto.YtxTemplateOut;
import com.thc.platform.modules.sms.dto.YtxTemplateSaveIn;
import com.thc.platform.modules.sms.entity.TemplateSignTypeEntity;
import com.thc.platform.modules.sms.entity.YtxTemplateEntity;
import com.thc.platform.modules.sms.service.TemplateSignTypeService;
import com.thc.platform.modules.sms.service.YtxTemplateService;

/**
 * 云通讯配置管理接口类
 */
@RestController
@RequestMapping("/sms/ytx/template")
public class YtxTemplateController {

	@Autowired
	private YtxTemplateService ytxTemplateService;
	@Autowired
	private TemplateSignTypeService templateSignTypeService;
	
	@PostMapping("/save")
	public Api<Object> saveAccount(@RequestBody YtxTemplateSaveIn in) {
		in.validate();
		ytxTemplateService.save(in);
		return Api.SUCCESS_RESULT;
	}

	@PostMapping("/list")
	public Api<List<YtxTemplateOut>> list(@RequestBody YtxTemplateListIn in) {
		in.validate();
		PageOut<YtxTemplateEntity> pageOut = ytxTemplateService.listPage(in);
		
		List<YtxTemplateOut> outs = new ArrayList<>();
		for(YtxTemplateEntity entity : pageOut.getItems()) {
			TemplateSignTypeEntity signType = templateSignTypeService.getById(entity.getSignTypeId());
			outs.add(new YtxTemplateOut(entity, signType));
		}
		
		return Api.ok(outs, pageOut.getTotalCount());
	}
	
	@GetMapping("/get")
	public Api<YtxTemplateEntity> list(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		YtxTemplateEntity entity = ytxTemplateService.getById(id);
		
		return Api.ok(entity);
	}
	
	@GetMapping("/delete")
	public Api<Object> delete(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		ytxTemplateService.removeById(id);
		
		return Api.SUCCESS_RESULT;
	}
}
