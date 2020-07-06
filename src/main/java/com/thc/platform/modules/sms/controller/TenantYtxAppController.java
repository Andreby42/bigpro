package com.thc.platform.modules.sms.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.api.GlobalPlatformApi;
import com.thc.platform.external.dto.TenantDto;
import com.thc.platform.external.dto.TenantQryByIdsIn;
import com.thc.platform.modules.sms.dto.TenantYtxAppRelBatchAddIn;
import com.thc.platform.modules.sms.dto.TenantYtxAppRelOut;
import com.thc.platform.modules.sms.entity.TenantYtxAppRelEntity;
import com.thc.platform.modules.sms.service.TenantYtxAppRelService;

/**
 * 云通讯应用与租户关系接口类
 */
@RestController
@RequestMapping("/sms/tenant-ytxapp-rel")
public class TenantYtxAppController {

	private Logger logger = LoggerFactory.getLogger(TenantYtxAppController.class);
	
	@Autowired
	private GlobalPlatformApi globalPlatformApi;
	
	@Autowired
	private TenantYtxAppRelService tenantYtxAppRelService;
	
	@PostMapping("/add")
	public Api<Object> saveRel(@RequestBody TenantYtxAppRelBatchAddIn in) {
		in.validate();
		
		tenantYtxAppRelService.add(in);
		
		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/deleteById")
	public Api<Object> deleteByid(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		tenantYtxAppRelService.removeById(id);

		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/list")
	public Api<List<TenantYtxAppRelOut>> list(@RequestParam("appId") String appId, @RequestHeader("x-access-token") String token) {
		if(StringUtil.isEmpty(appId))
			return Api.ok(null);
		
		List<TenantYtxAppRelEntity> entities = tenantYtxAppRelService.getByAppId(appId);
		List<Integer> ids = new ArrayList<>();
		for(TenantYtxAppRelEntity entity : entities)
			ids.add(entity.getTenantId());
		
		if(ids.isEmpty())
			return Api.ok(null);
		
		Map<Integer, TenantDto> tMap = getTenants(ids, token);
		List<TenantYtxAppRelOut> outs = new ArrayList<>();
		for(TenantYtxAppRelEntity entity : entities) {
			TenantDto tenant = tMap.get(entity.getTenantId());
			String tenantName = tenant != null ? tenant.getGroupName() : "未知";
			outs.add(new TenantYtxAppRelOut(entity, tenantName));
		}
		
		return Api.ok(outs);
	}
	
	private Map<Integer, TenantDto> getTenants(List<Integer> ids, String token) {
		Map<Integer, TenantDto> tMap = new HashMap<>();
		try {
			TenantQryByIdsIn in = new TenantQryByIdsIn();
			in.setIds(ids);
			
			Api<List<TenantDto>> result = globalPlatformApi.getTenantByIds(in, token);
			
			if(result.isSuccess() && result.getData() != null) {
				for(TenantDto tenant : result.getData())
					tMap.put(tenant.getThcTenantId(), tenant);
			}
		} catch (Exception e) {
			logger.error("invoke global-platform api error.", e);
		}
		return tMap;
	}
	
}
