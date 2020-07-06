package com.thc.platform.modules.ocr.biz.impl;

import com.thc.platform.modules.ocr.bean.OcrInvokeStatistical;
import com.thc.platform.modules.ocr.biz.IOcrInvokeStatisticalService;
import com.thc.platform.modules.ocr.dao.OcrInvokeStatisticalMapper;
import com.thc.platform.modules.ocr.util.DateTimeUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author @date             @version
 * zouyu            2020-02-01         1.0.0
 * *******************************************************
 * @Description: 服务实现层
 * <p>
 * *******************************************************
 * @update
 */
@Service
public class OcrInvokeStatisticalServiceImpl implements IOcrInvokeStatisticalService {

    @Resource
    private OcrInvokeStatisticalMapper mapper;

    /**
     * @param ocrInvokeStatistical 实体信息
     * @return 成功条数
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    @Transactional
    public int insertOcrInvokeStatistical(OcrInvokeStatistical ocrInvokeStatistical) {
        return mapper.insertOcrInvokeStatistical(ocrInvokeStatistical);
    }

    /**
     * @param id 主键ID
     * @return 实体信息
     * @Description: 通过ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public OcrInvokeStatistical getOcrInvokeStatisticalById(String id) {
        return mapper.getOcrInvokeStatisticalById(id);
    }

    /**
     * @param ocrInvokeStatistical 查询条件
     * @return 实体信息集合
     * @Description: 获取实体信息集合
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public List<OcrInvokeStatistical> getOcrInvokeStatisticals(OcrInvokeStatistical ocrInvokeStatistical) {
        this.buildQuery(ocrInvokeStatistical);
        return mapper.getOcrInvokeStatisticals(ocrInvokeStatistical);
    }

    /**
     * @param ocrInvokeStatistical 实体信息
     * @return 更新条数
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    @Transactional
    public int updateOcrInvokeStatistical(OcrInvokeStatistical ocrInvokeStatistical) {
        return mapper.updateOcrInvokeStatistical(ocrInvokeStatistical);
    }

    private void buildQuery(OcrInvokeStatistical ocrInvokeStatistical) {
        if (null != ocrInvokeStatistical) {
            Date statisticalStartDate = ocrInvokeStatistical.getStatisticalStartDate();
            Date statisticalEndDate = ocrInvokeStatistical.getStatisticalEndDate();
            if (null != statisticalStartDate)
                ocrInvokeStatistical.setStatisticalStartDate(DateTimeUtil.getFirstSecondOfDay(statisticalStartDate));
            if (null != statisticalEndDate)
                ocrInvokeStatistical.setStatisticalEndDate(DateTimeUtil.getLastSecondOfDay(statisticalEndDate));

        }
    }

    /**
     * @param ocrInvokeStatistical 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    public int getOcrInvokeStatisticalsNum(OcrInvokeStatistical ocrInvokeStatistical) {
        this.buildQuery(ocrInvokeStatistical);
        return mapper.getOcrInvokeStatisticalsNum(ocrInvokeStatistical);
    }

    /**
     * @param id 主键ID
     * @return 实体信息
     * @Description: 通过ID刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    @Transactional
    public void deleteOcrInvokeStatisticalById(String id) {
        mapper.deleteOcrInvokeStatisticalById(id);
    }

    @Override
    @Transactional
    public List<OcrInvokeStatistical> batchInsert(List<OcrInvokeStatistical> ocrInvokeStatisticals) {
        if (CollectionUtils.isNotEmpty(ocrInvokeStatisticals)) {
            ocrInvokeStatisticals.forEach(this::insertOcrInvokeStatistical);
        }
        return ocrInvokeStatisticals;
    }

}
