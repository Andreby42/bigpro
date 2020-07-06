package com.thc.platform.modules.express.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.express.dto.ExpressSearchIn;
import com.thc.platform.modules.express.entity.ExpressCompanyEntity;
import com.thc.platform.modules.express.repository.ExpressCompanyRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpressCompanyService extends ServiceImpl<ExpressCompanyRepository, ExpressCompanyEntity> {


    /**
     * 根据名称模糊查询快递公司
     * @param in ExpressSearchIn
     * @return map
     */
    public Map<String, Object> queryCom(ExpressSearchIn in) {
        LambdaQueryWrapper<ExpressCompanyEntity> wrapper = Wrappers.lambdaQuery();

        if(!StringUtil.isEmpty(in.getName())){
            wrapper.like(ExpressCompanyEntity::getComCode,in.getName()).or()
                    .like(ExpressCompanyEntity::getCom,in.getName());
        }
        int totalCount = baseMapper.selectCount(wrapper);
        wrapper.last("limit " + in.getOffset() + ", " + in.getPagesize());
        List<ExpressCompanyEntity> entityList =baseMapper.selectList(wrapper);
        Map<String,Object> resultMap = new HashMap<>();
        resultMap.put("resultList", entityList);
        resultMap.put("totalCount", totalCount);
        return resultMap;
    }
}
