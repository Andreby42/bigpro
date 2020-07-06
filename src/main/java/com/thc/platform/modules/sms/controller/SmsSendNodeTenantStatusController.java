package com.thc.platform.modules.sms.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.external.api.GlobalPlatformApi;
import com.thc.platform.external.dto.ListTenantByTypesIn;
import com.thc.platform.external.dto.TenantListOut;
import com.thc.platform.modules.sms.dto.ChangeTenantSendNodeStatusIn;
import com.thc.platform.modules.sms.dto.SmsSendNodeTenantStatusListIn;
import com.thc.platform.modules.sms.dto.SmsSendNodeTenantStatusListOut;
import com.thc.platform.modules.sms.entity.SmsSendNodeTenantStatusEntity;
import com.thc.platform.modules.sms.service.SmsSendNodeTenantStatusService;

@RestController
@RequestMapping("/sms/send-node/tanant-status/admin")
public class SmsSendNodeTenantStatusController {

	@Autowired
	private SmsSendNodeTenantStatusService smsSendNodeTenantStatusService;
	@Autowired
	private GlobalPlatformApi globalPlatformApi;
	
	@PostMapping("/changeStatus")
	public Api<Object> save(@RequestBody ChangeTenantSendNodeStatusIn in) {
		in.validate();
		smsSendNodeTenantStatusService.changeStatus(in);
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/listPage")
	public Api<List<SmsSendNodeTenantStatusListOut>> listPage(@RequestBody SmsSendNodeTenantStatusListIn in
			, @RequestHeader("x-access-token") String token) {
		in.validate();
		
		PageOut<TenantListOut> pageOut = getTenantList(
				in.getTenantName(), in.getOffset(), in.getPagesize(), token);
		
		List<SmsSendNodeTenantStatusListOut> outs = new ArrayList<>();
		for(TenantListOut tenant : pageOut.getItems()) {
			SmsSendNodeTenantStatusListOut out = new SmsSendNodeTenantStatusListOut();
			out.setTenantId(tenant.getThcTenantId());
			out.setTenantName(tenant.getTenantName());
			out.setSmsSendNodeId(in.getSmsSendNodeId());
			
			SmsSendNodeTenantStatusEntity nodeTenantStatus = smsSendNodeTenantStatusService.getByTenantIdAndNodeId(
					tenant.getThcTenantId(), in.getSmsSendNodeId());
			
			if(nodeTenantStatus == null)
				out.setStatus(SmsSendNodeTenantStatusEntity.STATUS_ON);
			else 
				out.setStatus(nodeTenantStatus.getStatus());
			
			outs.add(out);
		}
		return Api.ok(outs, pageOut.getTotalCount());
	}
	
	private PageOut<TenantListOut> getTenantList(String tenantName, Integer offset, Integer pagesize, String token) {
		ListTenantByTypesIn in = new ListTenantByTypesIn();
		in.setTenantName(tenantName);
		in.setOffset(offset);
		in.setPagesize(pagesize);
		
		List<Integer> tenantTypes = new ArrayList<>();
		tenantTypes.add(1); // 试用租户
		tenantTypes.add(2); // 正式租户
		in.setTenantTypes(tenantTypes);
		
		Api<List<TenantListOut>> result = globalPlatformApi.listTenantByTypes(in, token);
		
		return new PageOut<TenantListOut>(result.getData(), result.getTotalCount().intValue());
	}
	
}
