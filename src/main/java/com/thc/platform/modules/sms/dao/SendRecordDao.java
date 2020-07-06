package com.thc.platform.modules.sms.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thc.platform.modules.sms.dto.TenantOrgDaySendDto;
import com.thc.platform.modules.sms.entity.SendRecordEntity;

public interface SendRecordDao extends BaseMapper<SendRecordEntity> {

	@Select("SELECT sum(IFNULL(fee_num, 0)) fee_num, IFNULL( org_id, 'other' ) orgId, IFNULL( org_name, '其它' ) orgName " +
	            "FROM sms_send_record WHERE tenant_id = #{tenantId} and status = 1 and create_time LIKE concat(#{stDay},'%')" +
	            " GROUP BY org_id")
    List<TenantOrgDaySendDto> stTenantOrgDaySend(@Param("tenantId") Integer tenantId, @Param("stDay") String stDay);
	 
	@Select("SELECT DISTINCT(tenant_id) from sms_send_record WHERE status = 1 and create_time LIKE concat(#{stDay},'%')")
    List<Integer> getDisTenantIdByDay(@Param("stDay") String stDay);
	
	public List<Map<String, Object>> complexQry(@Param("tenantId") Integer tenantId
			, @Param("appCode") String appCode
			, @Param("appSerialNum") String appSerialNum
			, @Param("mobile") String mobile
			, @Param("offset") Integer offset
			, @Param("pagesize") Integer pagesize);
	
	public Integer complexQryCount(@Param("tenantId") Integer tenantId
			, @Param("appCode") String appCode
			, @Param("appSerialNum") String appSerialNum
			, @Param("mobile") String mobile);
}
