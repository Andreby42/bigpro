package com.thc.platform.modules.notice.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.notice.config.NoticeRabbitMqConfig;
import com.thc.platform.modules.notice.dto.NoticePushIn;
import com.thc.platform.modules.notice.dto.NoticeRecordOut;
import com.thc.platform.modules.notice.dto.NoticeSearchIn;
import com.thc.platform.modules.notice.entity.NoticeRecordEntity;
import com.thc.platform.modules.notice.service.NoticeRecordService;

/**
 * 通知
 */
@RestController
@RequestMapping("/notice")
public class NoticeController {

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private NoticeRecordService noticeRecordService;
	@Autowired
	private GlobalPlatformService globalPlatformService;
	
	@PostMapping("/push")
	public Api<Object> push(@RequestBody NoticePushIn in) {
		in.validate();
		
		rabbitTemplate.convertAndSend(NoticeRabbitMqConfig.EXCHANGE_NAME_RECEIVE, NoticeRabbitMqConfig.ROUTING_KEY, in);
		
		return Api.SUCCESS_RESULT;
	}
	
	@GetMapping("/getById")
    public Api<NoticeRecordOut> getById(@RequestParam("id") String id) {
		NoticeRecordEntity entity = noticeRecordService.getById(id);
    	if(entity == null)
    		throw BEUtil.failNormal("通知消息不存在");
    	
    	if(entity.getStatus() == NoticeRecordEntity.STATUS_UNREAD
    			|| entity.getStatus() == NoticeRecordEntity.STATUS_READED)
    		return Api.ok(new NoticeRecordOut(entity));
    	
    	throw BEUtil.failNormal("通知消息不存在");
    }
	
	@GetMapping("/getUnReadList")
    public Api<List<NoticeRecordOut>> getUnReadList(Integer offset, Integer pagesize) {
    	if(offset == null || offset < 0)
    		offset = 0;
    	
    	if(pagesize == null || pagesize < 0)
    		pagesize = 50;
    	
    	SysUserDto sysUser = globalPlatformService.getCurrentUser();
    	PageOut<NoticeRecordEntity> pageOut = noticeRecordService.getUnReadList(offset, pagesize, sysUser.getId());
    	List<NoticeRecordOut> outs = new ArrayList<>();
    	for(NoticeRecordEntity entity : pageOut.getItems())
    		outs.add(new NoticeRecordOut(entity));
    	
    	return Api.ok(outs, pageOut.getTotalCount());
    }
	
	@GetMapping("/search")
	public Api<List<NoticeRecordOut>> search(NoticeSearchIn in) {
		PageOut<NoticeRecordEntity> pageOut = noticeRecordService.search(in);
		
		List<NoticeRecordOut> outs = new ArrayList<>();
    	for(NoticeRecordEntity entity : pageOut.getItems())
    		outs.add(new NoticeRecordOut(entity));
    	
		return Api.ok(outs, pageOut.getTotalCount());
	}

	@GetMapping("/read")
	public Api<Object> read(String id) {
		noticeRecordService.read(id);
		return Api.SUCCESS_RESULT;
	}
	
}
