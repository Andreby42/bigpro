package com.thc.platform.modules.ocr.biz.impl;

import com.thc.platform.modules.ocr.bean.OcrInspectionResultItem;
import com.thc.platform.modules.ocr.biz.IOcrInspectionResultItemService;
import com.thc.platform.modules.ocr.dao.OcrInspectionResultItemMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
public class OcrInspectionResultItemServiceImpl implements IOcrInspectionResultItemService {

    @Resource
    private OcrInspectionResultItemMapper mapper;

    /**
     * @param ocrInspectionResultItem 实体信息
     * @return 成功条数
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    @Transactional
    public int insertOcrInspectionResultItem(OcrInspectionResultItem ocrInspectionResultItem) {
        return mapper.insertOcrInspectionResultItem(ocrInspectionResultItem);
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
    public OcrInspectionResultItem getOcrInspectionResultItemById(String id) {
        return mapper.getOcrInspectionResultItemById(id);
    }

    /**
     * @param ocrInspectionResultItem 查询条件
     * @return 实体信息集合
     * @Description: 获取实体信息集合
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public List<OcrInspectionResultItem> getOcrInspectionResultItems(OcrInspectionResultItem ocrInspectionResultItem) {
        List<OcrInspectionResultItem> ocrInspectionResultItems = mapper.getOcrInspectionResultItems(ocrInspectionResultItem);
        return ocrInspectionResultItems;
    }

    /**
     * @param ocrInspectionResultItem 实体信息
     * @return 更新条数
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    @Transactional
    public int updateOcrInspectionResultItem(OcrInspectionResultItem ocrInspectionResultItem) {
        return mapper.updateOcrInspectionResultItem(ocrInspectionResultItem);
    }

    /**
     * @param ocrInspectionResultItem 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    public int getOcrInspectionResultItemsNum(OcrInspectionResultItem ocrInspectionResultItem) {
        return mapper.getOcrInspectionResultItemsNum(ocrInspectionResultItem);
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
    public void deleteOcrInspectionResultItemById(String id) {
        mapper.deleteOcrInspectionResultItemById(id);
    }

    @Override
    @Transactional
    public List<OcrInspectionResultItem> batchInsert(List<OcrInspectionResultItem> ocrInspectionResultItems) {
        if (CollectionUtils.isNotEmpty(ocrInspectionResultItems)) {
            ocrInspectionResultItems.forEach(this::insertOcrInspectionResultItem);
            return ocrInspectionResultItems;
        }
        throw new RuntimeException("检验明细为空");
    }

    @Override
    public List<OcrInspectionResultItem> batchUpdate(List<OcrInspectionResultItem> ocrInspectionResultItems) {
        if (CollectionUtils.isNotEmpty(ocrInspectionResultItems)) {
            ocrInspectionResultItems.forEach(this::updateOcrInspectionResultItem);
            return ocrInspectionResultItems;
        }
        throw new RuntimeException("检验明细为空");
    }

    @Override
    @Transactional
    public List<OcrInspectionResultItem> deleteAndModify(List<OcrInspectionResultItem> ocrInspectionResultItems) {
        if (CollectionUtils.isEmpty(ocrInspectionResultItems)) throw new RuntimeException("检验结果明细为空");
        String ocrInspectionResultId = ocrInspectionResultItems.get(0).getOcrInspectionResultId();
        mapper.deleteByOcrInspectionResultId(ocrInspectionResultId);
        ocrInspectionResultItems.forEach(r -> r.setId(UUID.randomUUID().toString()));
        this.batchInsert(ocrInspectionResultItems);
        return ocrInspectionResultItems;
    }

    @Override
    public List<OcrInspectionResultItem> getByOcrInspectionResultId(String ocrInspectionResultId) {
        if (StringUtils.isNotEmpty(ocrInspectionResultId)) {
            return mapper.getByOcrInspectionResultId(ocrInspectionResultId);
        }
        return null;
    }

    @Override
    public List<OcrInspectionResultItem> getByOcrInspectionResultIdIn(List<String> ids) {
        if (CollectionUtils.isNotEmpty(ids)) {
            return mapper.findByResultIdIn(ids);
        }
        return Collections.emptyList();
    }

}                                                                                                        
