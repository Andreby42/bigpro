package com.thc.platform.modules.sms.dao;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thc.platform.modules.sms.entity.SysTemplateEntity;

public interface SysTemplateDao extends BaseMapper<SysTemplateEntity> {

	List<SysTemplateEntity> listPage(Integer tenantId
			, String code
			, Integer smsType
			, String name
			, Integer offset
			, Integer pagesize);
	
	Integer listPageCount(Integer tenantId
			, String code
			, Integer smsType
			, String name);
	
}
