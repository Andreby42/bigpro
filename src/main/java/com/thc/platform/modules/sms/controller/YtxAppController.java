package com.thc.platform.modules.sms.controller;

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
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.sms.dto.YtxAppChgWhiteStatusIn;
import com.thc.platform.modules.sms.dto.YtxAppListIn;
import com.thc.platform.modules.sms.dto.YtxAppSaveIn;
import com.thc.platform.modules.sms.entity.YtxAppEntity;
import com.thc.platform.modules.sms.service.TenantYtxAppRelService;
import com.thc.platform.modules.sms.service.YtxAppService;
import com.thc.platform.modules.sms.service.YtxTemplateService;

/**
 * 云通讯配置管理接口类
 */
@RestController
@RequestMapping("/sms/ytx/app")
public class YtxAppController {

	@Autowired
	private YtxAppService ytxAppService;
	@Autowired
	private YtxTemplateService ytxTemplateService;
	@Autowired
	private TenantYtxAppRelService tenantYtxAppRelService;
	
	@PostMapping("/save")
	public Api<Object> saveAccount(@RequestBody YtxAppSaveIn in) {
		in.validate();
		ytxAppService.save(in);
		return Api.SUCCESS_RESULT;
	}

	@PostMapping("/list")
	public Api<List<YtxAppEntity>> list(@RequestBody YtxAppListIn in) {
		in.validate();
		PageOut<YtxAppEntity> pageOut = ytxAppService.listPage(in);
		
		return Api.ok(pageOut.getItems(), pageOut.getTotalCount());
	}
	
	@GetMapping("/get")
	public Api<YtxAppEntity> list(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		YtxAppEntity entity = ytxAppService.getById(id);
		
		return Api.ok(entity);
	}
	
	@GetMapping("/delete")
	public Api<Object> delete(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		if(ytxTemplateService.countByAppId(id) > 0)
			throw BEUtil.failNormal("请先删除应用下模版");
		
		if(tenantYtxAppRelService.countByAppId(id) > 0)
			throw BEUtil.failNormal("尚有应用使用此应用");
		
		ytxAppService.removeById(id);
		
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/changeWhiteListStatus")
	public Api<Object> changeWhiteListStatus(@RequestBody YtxAppChgWhiteStatusIn in) {
		in.validate();
		ytxAppService.changeWhiteListStatus(in);
		
		return Api.SUCCESS_RESULT;
	}
	
}
