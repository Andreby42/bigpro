package com.thc.platform.modules.apppush.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.apppush.dto.AndroidPushIn;
import com.thc.platform.modules.apppush.dto.IosTransmissionWithApnIn;
import com.thc.platform.modules.apppush.dto.TransmissionIn;
import com.thc.platform.modules.apppush.service.PushService;

/**
 * 短信发送接口类
 */
@RestController
@RequestMapping("/app-push/exact")
public class ExactPushController {
	
	@Autowired
	private PushService pushService;
	
	@PostMapping("/iosPush")
	public Api<Object> iosPush(@RequestBody IosTransmissionWithApnIn in) {
		in.validate();
		
		pushService.pushSingle(in);
		
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/androidPush")
	public Api<Object> androidPush(@RequestBody AndroidPushIn in) {
		in.validate();
		
		pushService.pushSingle(in);
		
		return Api.SUCCESS_RESULT;
	}
	
	@PostMapping("/transmissionPush")
	public Api<Object> transmissionPush(@RequestBody TransmissionIn in) {
		in.validate();
		
		pushService.pushSingle(in);
		
		return Api.SUCCESS_RESULT;
	}
}
