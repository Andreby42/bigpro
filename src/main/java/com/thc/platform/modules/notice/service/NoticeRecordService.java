package com.thc.platform.modules.notice.service;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.notice.dao.NoticeRecordDao;
import com.thc.platform.modules.notice.dto.NoticeSearchIn;
import com.thc.platform.modules.notice.entity.NoticeRecordEntity;

@Service
public class NoticeRecordService extends ServiceImpl<NoticeRecordDao, NoticeRecordEntity> {

	private Logger logger = LoggerFactory.getLogger(NoticeRecordService.class);
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private GlobalPlatformService globalPlatformService;
	@Autowired
	private NoticePushService noticePushService;
	
	@GetMapping("/getUnReadList")
    public PageOut<NoticeRecordEntity> getUnReadList(Integer offset, Integer pagesize, String receiverId) {
		LambdaQueryWrapper<NoticeRecordEntity> wrapper = Wrappers.lambdaQuery();
		
		wrapper.eq(NoticeRecordEntity::getReceiverId, receiverId)
	 		.eq(NoticeRecordEntity::getStatus, NoticeRecordEntity.STATUS_UNREAD);
	 
		Integer totalCount = baseMapper.selectCount(wrapper);
		 
		wrapper.orderByDesc(NoticeRecordEntity::getCreateTime)
		 	.last("limit " + offset + "," + pagesize);
		 
		List<NoticeRecordEntity> resultList = baseMapper.selectList(wrapper);
		 
    	return new PageOut<NoticeRecordEntity>(resultList, totalCount);
    }
	
	
	public PageOut<NoticeRecordEntity> search(NoticeSearchIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		
		LambdaQueryWrapper<NoticeRecordEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(NoticeRecordEntity::getReceiverId, sysUser.getId());
	
		if(in.getStartTime() != null)
			wrapper.ge(NoticeRecordEntity::getCreateTime, in.getStartTime());
		
		
		if(in.getEndTime() != null) {
			 Calendar calendar = Calendar.getInstance();
			 calendar.setTime(in.getEndTime());
			 calendar.set(Calendar.HOUR_OF_DAY, 23);
			 calendar.set(Calendar.MINUTE, 59);
			 calendar.set(Calendar.SECOND, 59);
			 
			 wrapper.le(NoticeRecordEntity::getCreateTime, calendar.getTime());
		 }
		
		Integer totalCount = baseMapper.selectCount(wrapper);
		 
		wrapper.orderByDesc(NoticeRecordEntity::getCreateTime)
		 	.last("limit " + in.getOffset() + "," + in.getPagesize());
		 	
		List<NoticeRecordEntity> resultList = baseMapper.selectList(wrapper);
		
		return new PageOut<NoticeRecordEntity>(resultList, totalCount);
	}
	
	public void read(String id) {
		NoticeRecordEntity entity = this.getById(id);
		if(entity == null)
			return;
		
		if(entity.getConcurrent() == null 
				|| entity.getConcurrent() == NoticeRecordEntity.CONCURRENT_TRUE)
			readNormal(id);
		else 
			readAndRecall(entity.getId(), entity.getAppCode(), entity.getAppSerialNum(), entity.getReceiverId());
	}
	
	private void readNormal(String id) {
		this.update(
				Wrappers.<NoticeRecordEntity>lambdaUpdate()
					.set(NoticeRecordEntity::getStatus, NoticeRecordEntity.STATUS_READED)
					.eq(NoticeRecordEntity::getId, id)
					.eq(NoticeRecordEntity::getStatus, NoticeRecordEntity.STATUS_UNREAD)
				);
	}
	
	private void readAndRecall(String noticeId, String appCode, String appSerialNum, String receiverId) {
		String key = "GLOBAL:PLATFORM_EXTEND:NOTICE:GROUP_EXECUTE_LOCK:" + appCode + ":" + appSerialNum;
		Boolean locked = redisTemplate.opsForValue().setIfAbsent(key, receiverId);
		if(locked) {
			try {
				NoticeRecordEntity entity = this.getById(noticeId);
				if(entity.getStatus() != NoticeRecordEntity.STATUS_UNREAD)
					return;
				
				// 更新当前通知状态为：已读
				this.update(
						Wrappers.<NoticeRecordEntity>lambdaUpdate()
							.set(NoticeRecordEntity::getStatus, NoticeRecordEntity.STATUS_READED)
							.eq(NoticeRecordEntity::getId, noticeId)
						);
				// 更新同组的其他通知状态为：撤回
				this.update(
						Wrappers.<NoticeRecordEntity>lambdaUpdate()
							.set(NoticeRecordEntity::getStatus, NoticeRecordEntity.STATUS_RECALL)
							.set(NoticeRecordEntity::getNotes, "已执行。执行receiverId: " + receiverId)
							.eq(NoticeRecordEntity::getAppCode, appCode)
							.eq(NoticeRecordEntity::getAppSerialNum, appSerialNum)
							.ne(NoticeRecordEntity::getId, noticeId)
						);
				// 推送撤回消息到：同组的其他通知
				List<NoticeRecordEntity> entities = baseMapper.selectList(
						Wrappers.<NoticeRecordEntity>lambdaQuery()
							.eq(NoticeRecordEntity::getAppCode, appCode)
							.eq(NoticeRecordEntity::getAppSerialNum, appSerialNum)
							.ne(NoticeRecordEntity::getId, noticeId)
						);
				
				String pushContent = noticePushService.getNticeRecallPushContent(entity.getTemplateCode());
				for(NoticeRecordEntity recallNotice : entities) {
					NoticeRecordEntity temp = new NoticeRecordEntity();
					temp.setId(recallNotice.getId());
					try {
						noticePushService.pushMsgTip(pushContent, receiverId);
						temp.setPushRecallMqStatus(NoticeRecordEntity.PUSH_MQ_STATUS_SUCCESS);
					} catch (Exception e) {
						temp.setPushRecallMqStatus(NoticeRecordEntity.PUSH_MQ_STATUS_FAIL);
						logger.error("推送recall mq失败", e);
					}
					this.updateById(temp);
				}
				
			} catch (Exception e) {
				logger.error("处理消息撤回错误", e);
				redisTemplate.delete(key);
			} finally {
				redisTemplate.expire(key, 10, TimeUnit.MINUTES);
			}
		} else {
			throw BEUtil.failNormal("已被其他人处理");
		}
	}
	
}
