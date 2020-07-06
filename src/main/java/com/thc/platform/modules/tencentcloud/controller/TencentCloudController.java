package com.thc.platform.modules.tencentcloud.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.tencentcloud.service.TencentCloudService;

@RestController
@RequestMapping("/tencentcloud")
public class TencentCloudController {

	@Autowired
	private TencentCloudService tencentCloudService;
	
	@GetMapping("/getUploadSignature")
	public Api<String> getUploadSignature(@RequestParam(value = "classId", required = false) String classId) {
		
		String signature = tencentCloudService.getSignature(classId);
		
		return Api.ok(signature);
	}
	
}
