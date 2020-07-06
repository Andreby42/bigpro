package com.thc.platform.modules.sms.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
import com.thc.platform.modules.sms.dto.SysTemplateListIn;
import com.thc.platform.modules.sms.dto.SysTemplateListOut;
import com.thc.platform.modules.sms.dto.SysTemplateSaveIn;
import com.thc.platform.modules.sms.dto.SysTemplateTenantVerSaveIn;
import com.thc.platform.modules.sms.entity.SysTemplateEntity;
import com.thc.platform.modules.sms.entity.SysTemplateTenantVerEntity;
import com.thc.platform.modules.sms.service.SysTemplateService;
import com.thc.platform.modules.sms.service.SysTemplateTenantVerService;

/**
 * 系统模版配置管理接口类
 */
@RestController
@RequestMapping("/sms/sys-template")
public class SysTemplateController {

	@Autowired
	private SysTemplateService sysTemplateService;
	@Autowired
	private SysTemplateTenantVerService sysTemplateTenantVerService;
	
	@PostMapping("/save")
	public Api<Object> save(@RequestBody SysTemplateSaveIn in) {
		in.validate();
		sysTemplateService.save(in);
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/saveTenantVer")
	public Api<Object> saveTenantVer(@RequestBody SysTemplateTenantVerSaveIn in) {
		in.validate();
		sysTemplateTenantVerService.save(in);
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/listStandards")
	public Api<List<SysTemplateListOut>> listStandards(@RequestBody SysTemplateListIn in) {
		in.validate();
		
		PageOut<SysTemplateEntity> pageOut = sysTemplateService.listPage(in);
		List<SysTemplateListOut> outs = new ArrayList<>();
		for(SysTemplateEntity entity : pageOut.getItems()) 
			outs.add(new SysTemplateListOut(entity));
		
		return Api.ok(outs, pageOut.getTotalCount());
	}

	@PostMapping("/listTenants")
	public Api<List<Map<String, Object>>> listTenants(@RequestBody SysTemplateListIn in) {
		in.validate();
		
		PageOut<Map<String, Object>> pageOut = sysTemplateTenantVerService.listPage(in);
		
		return Api.ok(pageOut.getItems(), pageOut.getTotalCount());
	}
	
	@GetMapping("/get")
	public Api<SysTemplateEntity> get(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		SysTemplateEntity entity = sysTemplateService.getById(id);
		
		return Api.ok(entity);
	}
	
	@GetMapping("/getTenantVer")
	public Api<SysTemplateTenantVerEntity> getTenantVer(@RequestParam("sysTemplateId") String sysTemplateId, @RequestParam("tenantId") Integer tenantId) {
		if(StringUtil.isEmpty(sysTemplateId) || tenantId == null)
			return Api.ok(null);
		
		SysTemplateTenantVerEntity entity = sysTemplateTenantVerService.get(sysTemplateId, tenantId);
		
		return Api.ok(entity);
	}
	
	@GetMapping("/delete")
	public Api<Object> delete(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		if(sysTemplateTenantVerService.countTenantVer(id) > 0)
			throw BEUtil.failNormal("删除失败，存在租户版本");
		
		sysTemplateService.removeById(id);
		
		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/deleteTenantVer")
	public Api<Object> deleteTenantVer(@RequestParam("sysTemplateId") String sysTemplateId, @RequestParam("tenantId") Integer tenantId) {
		if(StringUtil.isEmpty(sysTemplateId) || tenantId == null)
			return Api.ok(null);
		
		sysTemplateTenantVerService.delete(sysTemplateId, tenantId);
		
		return Api.SUCCESS_RESULT;
	}
}
