package com.thc.platform.modules.sms.controller;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.BeanUtil;
import com.thc.platform.modules.sms.dto.SmsSendNodeListPageIn;
import com.thc.platform.modules.sms.dto.SmsSendNodeListPageOut;
import com.thc.platform.modules.sms.dto.SmsSendNodeOut;
import com.thc.platform.modules.sms.dto.SmsSendNodeSaveIn;
import com.thc.platform.modules.sms.entity.SmsSendNodeEntity;
import com.thc.platform.modules.sms.service.SmsSendNodeService;

@RestController
@RequestMapping("/sms/send-node/admin")
public class SmsSendNodeController {

	@Autowired
	private SmsSendNodeService smsSendNodeService;
	
	@PostMapping("/save")
	public Api<String> save(@RequestBody SmsSendNodeSaveIn in) {
		in.validate();
		String id = smsSendNodeService.save(in);
		return Api.ok(id);
	}
	
	@GetMapping("/getById")
	public Api<SmsSendNodeOut> getById(@RequestParam("id") String id) {
		SmsSendNodeEntity entity = smsSendNodeService.getById(id);
		SmsSendNodeOut out = new SmsSendNodeOut();
		BeanUtils.copyProperties(entity, out);
		return Api.ok(out);
	}
	
	@PostMapping("/listPage")
	public Api<List<SmsSendNodeListPageOut>> listPage(@RequestBody SmsSendNodeListPageIn in) {
		in.validate();
		PageOut<SmsSendNodeEntity> pageOut = smsSendNodeService.listPage(in);
		List<SmsSendNodeListPageOut> outs = BeanUtil.listCopy(pageOut.getItems(), SmsSendNodeListPageOut.class);
		return Api.ok(outs, pageOut.getTotalCount());
	}
	
	@GetMapping("/deleteById")
	public Api<Object> deleteById(@RequestParam("id") String id) {
		smsSendNodeService.removeById(id);
		return Api.SUCCESS_RESULT;
	}
	
}
