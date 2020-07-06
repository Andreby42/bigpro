package com.thc.platform.modules.help.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.BeanUtil;
import com.thc.platform.modules.help.dto.HelpFeedbackListIn;
import com.thc.platform.modules.help.dto.HelpFeedbackOut;
import com.thc.platform.modules.help.entity.HelpFeedbackEntity;
import com.thc.platform.modules.help.service.HelpFeedbackService;

/**
 * 帮助编辑相关接口
 */
@RestController
@RequestMapping("/help/admin/feedback")
public class HelpFeedbackController {

	@Autowired
	private HelpFeedbackService helpFeedbackService;
	
	@PostMapping("/list")
	public Api<List<HelpFeedbackOut>> list(@RequestBody HelpFeedbackListIn in) {
		in.validate();
		PageOut<HelpFeedbackEntity> pageOut = helpFeedbackService.listPage(in);
		List<HelpFeedbackOut> outs = BeanUtil.listCopy(pageOut.getItems(), HelpFeedbackOut.class);
		return Api.ok(outs, pageOut.getTotalCount());
	}
	
}
