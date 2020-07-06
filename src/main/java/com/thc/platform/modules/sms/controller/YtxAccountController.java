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
import com.thc.platform.modules.sms.dto.YtxAccountListIn;
import com.thc.platform.modules.sms.dto.YtxAccountSaveIn;
import com.thc.platform.modules.sms.entity.YtxAccountEntity;
import com.thc.platform.modules.sms.service.YtxAccountService;
import com.thc.platform.modules.sms.service.YtxAppService;

/**
 * 云通讯配置管理接口类
 */
@RestController
@RequestMapping("/sms/ytx/account")
public class YtxAccountController {

	@Autowired
	private YtxAccountService ytxAccountService;
	@Autowired
	private YtxAppService ytxAppService;
	
	@PostMapping("/save")
	public Api<Object> saveAccount(@RequestBody YtxAccountSaveIn in) {
		in.validate();
		ytxAccountService.save(in);
		return Api.SUCCESS_RESULT;
	}

	@PostMapping("/list")
	public Api<List<YtxAccountEntity>> list(@RequestBody YtxAccountListIn in) {
		in.validate();
		PageOut<YtxAccountEntity> pageOut = ytxAccountService.listPage(in);
		
		return Api.ok(pageOut.getItems(), pageOut.getTotalCount());
	}
	
	@GetMapping("/get")
	public Api<YtxAccountEntity> list(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		YtxAccountEntity entity = ytxAccountService.getById(id);
		
		return Api.ok(entity);
	}
	
	@GetMapping("/delete")
	public Api<Object> delete(@RequestParam("id") String id) {
		if(StringUtil.isEmpty(id))
			return Api.ok(null);
		
		if(ytxAppService.countByAccountId(id) > 0)
			throw BEUtil.failNormal("请先删除账号下应用");
		
		ytxAccountService.removeById(id);
		
		return Api.SUCCESS_RESULT;
	}
}
