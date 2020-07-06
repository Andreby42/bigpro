package com.thc.platform.modules.express.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.thc.platform.modules.express.dto.OrgDaySearch;
import com.thc.platform.modules.express.entity.ExpressSearchHisEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
*  express_search_his
* @author Yapu 2019-11-12
*/
@Repository
public interface ExpressSearchHisRepository extends BaseMapper<ExpressSearchHisEntity> {
    @Select("SELECT count(id) daySearchNum, IFNULL( org_id, 'other' ) orgId,IFNULL( org_name, '其它' ) orgName " +
            "FROM express_search_his WHERE tenant_id = #{tenantId} and create_time LIKE concat(#{stDay},'%')" +
            " GROUP BY org_id")
    List<OrgDaySearch> getSearchCountByDay(@Param("tenantId") String tenantId,
                                           @Param("stDay") String stDay);

    @Select("SELECT DISTINCT(tenant_id) from express_search_his WHERE create_time LIKE concat(#{stDay},'%')")
    List<String> getDisTenantIdByDay(@Param("stDay") String stDay);
}
