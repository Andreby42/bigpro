package com.thc.platform.modules.ocr.biz.impl;

import com.thc.platform.modules.ocr.bean.OcrInspectionResult;
import com.thc.platform.modules.ocr.bean.OcrInspectionResultItem;
import com.thc.platform.modules.ocr.bean.req.OcrInspectionResultReq;
import com.thc.platform.modules.ocr.biz.IOcrInspectionResultItemService;
import com.thc.platform.modules.ocr.biz.IOcrInspectionResultService;
import com.thc.platform.modules.ocr.dao.OcrInspectionResultMapper;
import com.thc.platform.modules.ocr.util.constant.OcrConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
public class OcrInspectionResultServiceImpl implements IOcrInspectionResultService {

    @Resource
    private OcrInspectionResultMapper mapper;
    @Resource
    private IOcrInspectionResultItemService ocrInspectionResultItemService;

    /**
     * @param ocrInspectionResult 实体信息
     * @return 成功条数
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public int insertOcrInspectionResult(OcrInspectionResult ocrInspectionResult) {
        return mapper.insertOcrInspectionResult(ocrInspectionResult);
    }

    /**
     * @param id 主键ID TODO
     * @return 实体信息
     * @Description: 通过ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public OcrInspectionResult getOcrInspectionResultById(String id) {
        OcrInspectionResult ocrInspectionResult = mapper.getOcrInspectionResultById(id);
        if (null != ocrInspectionResult) {
            List<OcrInspectionResultItem> ocrInspectionResultItems = ocrInspectionResultItemService.getByOcrInspectionResultId(id);
            ocrInspectionResult.setOcrInspectionResultItems(ocrInspectionResultItems);
        }
        return ocrInspectionResult;
    }

    /**
     * @param ocrInspectionResult 查询条件
     * @return 实体信息集合
     * @Description: 获取实体信息集合
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public List<OcrInspectionResult> getOcrInspectionResults(OcrInspectionResultReq ocrInspectionResult) {
        List<OcrInspectionResult> ocrInspectionResults = mapper.getOcrInspectionResults(ocrInspectionResult);
        if (CollectionUtils.isNotEmpty(ocrInspectionResults)) {
            List<String> ids = ocrInspectionResults.stream().map(OcrInspectionResult::getId).collect(Collectors.toList());
            List<OcrInspectionResultItem> items = ocrInspectionResultItemService.getByOcrInspectionResultIdIn(ids);
            Map<String, List<OcrInspectionResultItem>> resultItemMap = items.stream().collect(Collectors.groupingBy(OcrInspectionResultItem::getOcrInspectionResultId));
            ocrInspectionResults.forEach(r -> r.setOcrInspectionResultItems(resultItemMap.get(r.getId())));
        }
        return ocrInspectionResults;
    }

    /**
     * @param ocrInspectionResult 实体信息
     * @return 更新条数
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public int updateOcrInspectionResult(OcrInspectionResult ocrInspectionResult) {
        return mapper.updateOcrInspectionResult(ocrInspectionResult);
    }

    /**
     * @param ocrInspectionResult 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    public int getOcrInspectionResultsNum(OcrInspectionResultReq ocrInspectionResult) {
        return mapper.getOcrInspectionResultsNum(ocrInspectionResult);
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
    public void deleteOcrInspectionResultById(String id) {
        mapper.deleteOcrInspectionResultById(id);
    }

    @Override
    @Transactional
    public OcrInspectionResult userModify(OcrInspectionResult ocrInspectionResult) {
        if (null == ocrInspectionResult) throw new RuntimeException("检验结果为空,不能保存");
        String id = ocrInspectionResult.getId();
        if (StringUtils.isNotEmpty(id)) {
            OcrInspectionResult currentInspectionResult = this.getOcrInspectionResultById(id);
            if (currentInspectionResult == null ||
                    !OcrConstants.ResultType.MANUAL.equals(currentInspectionResult.getResultType())) {
                ocrInspectionResult.setId(UUID.randomUUID().toString());
                this.insertOcrInspectionResult(ocrInspectionResult);
            } else {
                // 重置更新时间
                ocrInspectionResult.setUpdateTime(null);
                this.updateOcrInspectionResult(ocrInspectionResult);
            }
        } else {
            ocrInspectionResult.setId(UUID.randomUUID().toString());
            this.insertOcrInspectionResult(ocrInspectionResult);
        }
        return ocrInspectionResult;
    }

    @Override
    public List<OcrInspectionResult> getByOcrRecordIdIn(List<String> ocrRecordIds) {
        if (CollectionUtils.isNotEmpty(ocrRecordIds)) {
            return mapper.getByOcrRecordIdIn(ocrRecordIds);
        }
        return Collections.emptyList();
    }

    @Override
    public List<OcrInspectionResult> getByOcrRecordId(String ocrRecordId) {
        List<OcrInspectionResult> ocrInspectionResults = mapper.getByOcrRecordId(ocrRecordId);
        if (CollectionUtils.isNotEmpty(ocrInspectionResults)) {
            ocrInspectionResults.forEach(r -> {
                String id = r.getId();
                List<OcrInspectionResultItem> ocrInspectionResultItems = ocrInspectionResultItemService.getByOcrInspectionResultId(id);
                r.setOcrInspectionResultItems(ocrInspectionResultItems);
            });
        }
        return ocrInspectionResults;
    }

}                                                                                                        
