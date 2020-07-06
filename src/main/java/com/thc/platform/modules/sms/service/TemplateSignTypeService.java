package com.thc.platform.modules.sms.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.modules.sms.dao.TemplateSignTypeDao;
import com.thc.platform.modules.sms.entity.TemplateSignTypeEntity;

/**
 * 云通讯模版签名服务
 */
@Service
public class TemplateSignTypeService extends ServiceImpl<TemplateSignTypeDao, TemplateSignTypeEntity> {

	public List<TemplateSignTypeEntity> getAll() {
		return baseMapper.selectList(Wrappers.<TemplateSignTypeEntity>lambdaQuery());
	}
	
}
