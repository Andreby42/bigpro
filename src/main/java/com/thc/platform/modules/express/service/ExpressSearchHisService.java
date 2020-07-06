package com.thc.platform.modules.express.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.api.GlobalPlatformApi;
import com.thc.platform.external.dto.AccountInfoIn;
import com.thc.platform.external.dto.AccountInfoOut;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.express.dto.ExpressResp;
import com.thc.platform.modules.express.dto.ExpressSearchIn;
import com.thc.platform.modules.express.dto.OrgDaySearch;
import com.thc.platform.modules.express.entity.ExpressSearchHisEntity;
import com.thc.platform.modules.express.repository.ExpressSearchHisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class ExpressSearchHisService extends ServiceImpl<ExpressSearchHisRepository, ExpressSearchHisEntity> {

    @Resource
    private GlobalPlatformService globalPlatformService;
    /**
     * 添加查询记录
     *  @param in
     * @param postParameters map
     * @param resp
     */
    public void addQueryRecord(ExpressSearchIn in, MultiValueMap<String, Object> postParameters, ExpressResp resp) {
        ExpressSearchHisEntity entity = new ExpressSearchHisEntity();
        entity.setId(ShortUUID.uuid());
        entity.setReq(postParameters.toString());
        entity.setCom(in.getCom());
        entity.setNum(in.getNum());
        entity.setCreateTime(new Date());
        entity.setTenantId(in.getTenantId());
        entity.setOrgId(in.getOrganizationId());
        entity.setOrgName(in.getOrganizationName());
        entity.setCreator(in.getDisplayName());

        if (resp != null) {
            entity.setResp(JSON.toJSONString(resp));
        }
        this.save(entity);
    }


    public List<OrgDaySearch> getSearchCountByDay(String tenantId, String stDay) {
        return baseMapper.getSearchCountByDay(tenantId,stDay);
    }

    public List<String> getDisTenantIdByDay(String stDay) {
        return baseMapper.getDisTenantIdByDay(stDay);
    }

    public Comparable<BigDecimal> getTenantAccountBalance(ExpressSearchIn in) {
        AccountInfoIn accountInfoIn = new AccountInfoIn();
        accountInfoIn.setTenantId(in.getTenantId());
        accountInfoIn.setTenantName(in.getTenantName());
        AccountInfoOut out = globalPlatformService.getTenantAccountBalance(accountInfoIn);
        if(out!=null){
            return out.getBalance();
        }
        return new BigDecimal(0);
    }
}
