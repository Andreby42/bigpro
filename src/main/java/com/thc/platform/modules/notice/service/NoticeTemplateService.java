package com.thc.platform.modules.notice.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.modules.notice.dao.NoticeTemplateDao;
import com.thc.platform.modules.notice.entity.NoticeTemplateEntity;

@Service
public class NoticeTemplateService extends ServiceImpl<NoticeTemplateDao, NoticeTemplateEntity> {

	public NoticeTemplateEntity getByCode(String code) {
		return baseMapper.selectOne(
				Wrappers.<NoticeTemplateEntity>lambdaQuery()
					.eq(NoticeTemplateEntity::getCode, code)
			);
	}
	
}
