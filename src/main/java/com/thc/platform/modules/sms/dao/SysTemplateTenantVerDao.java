package com.thc.platform.modules.sms.dao;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thc.platform.modules.sms.entity.SysTemplateTenantVerEntity;

public interface SysTemplateTenantVerDao extends BaseMapper<SysTemplateTenantVerEntity> {

	
	List<Map<String, Object>> listPage(Integer tenantId
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
