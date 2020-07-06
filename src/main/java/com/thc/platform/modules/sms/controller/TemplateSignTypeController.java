package com.thc.platform.modules.sms.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.sms.entity.TemplateSignTypeEntity;
import com.thc.platform.modules.sms.service.TemplateSignTypeService;

/**
 * 模版签名接口类
 */
@RestController
@RequestMapping("/sms/template-signtype")
public class TemplateSignTypeController {

	@Autowired
	private TemplateSignTypeService templateSignTypeService;
	
	@GetMapping("/listAll")
	public Api<List<TemplateSignTypeEntity>> list() {
		List<TemplateSignTypeEntity> entities = templateSignTypeService.getAll();
		return Api.ok(entities);
	}
	
}
