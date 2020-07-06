package com.thc.platform.modules.ocr.biz.impl;

import com.thc.platform.modules.ocr.bean.OcrExamineResult;
import com.thc.platform.modules.ocr.bean.req.OcrExamineResultReq;
import com.thc.platform.modules.ocr.biz.IOcrExamineResultService;
import com.thc.platform.modules.ocr.dao.OcrExamineResultMapper;
import com.thc.platform.modules.ocr.util.constant.OcrConstants;
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
public class OcrExamineResultServiceImpl implements IOcrExamineResultService {

    @Resource
    private OcrExamineResultMapper mapper;

    /**
     * @param ocrExamineResult 实体信息
     * @return 成功条数
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public int insertOcrExamineResult(OcrExamineResult ocrExamineResult) {
        return mapper.insertOcrExamineResult(ocrExamineResult);
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
    public OcrExamineResult getOcrExamineResultById(String id) {
        return mapper.getOcrExamineResultById(id);
    }

    /**
     * @param ocrExamineResult 查询条件
     * @return 实体信息集合
     * @Description: 获取实体信息集合
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public List<OcrExamineResult> getOcrExamineResults(OcrExamineResultReq ocrExamineResult) {
        List<OcrExamineResult> ocrExamineResults = mapper.getOcrExamineResults(ocrExamineResult);
        return ocrExamineResults;
    }

    /**
     * @param ocrExamineResult 实体信息
     * @return 更新条数
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public int updateOcrExamineResult(OcrExamineResult ocrExamineResult) {
        return mapper.updateOcrExamineResult(ocrExamineResult);
    }

    /**
     * @param ocrExamineResult 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    public int getOcrExamineResultsNum(OcrExamineResultReq ocrExamineResult) {
        return mapper.getOcrExamineResultsNum(ocrExamineResult);
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
    public void deleteOcrExamineResultById(String id) {
        mapper.deleteOcrExamineResultById(id);
    }

    @Override
    @Transactional
    public OcrExamineResult userModify(OcrExamineResult ocrExamineResult) {
        if (null == ocrExamineResult) throw new RuntimeException("检查结果为空");
        String id = ocrExamineResult.getId();
        if (StringUtils.isNotEmpty(id)) {
            OcrExamineResult currentExamineResult = this.getOcrExamineResultById(id);
            if (currentExamineResult == null ||
                    !OcrConstants.ResultType.MANUAL.equals(currentExamineResult.getResultType())) {
                ocrExamineResult.setId(UUID.randomUUID().toString());
                this.insertOcrExamineResult(ocrExamineResult);
            } else {
                this.updateOcrExamineResult(ocrExamineResult);
            }
            ocrExamineResult.setUpdateTime(null);
            this.updateOcrExamineResult(ocrExamineResult);
        } else {
            id = UUID.randomUUID().toString();
            ocrExamineResult.setId(id);
            this.insertOcrExamineResult(ocrExamineResult);
        }
        return ocrExamineResult;
    }

    @Override
    public List<OcrExamineResult> getByOcrRecordIdIn(List<String> ocrRecordIds) {
        if (CollectionUtils.isNotEmpty(ocrRecordIds)) {
            return mapper.getByOcrRecordIdIn(ocrRecordIds);
        }
        return Collections.emptyList();
    }

    @Override
    public List<OcrExamineResult> getByOcrRecordId(String ocrRecordId) {
        return mapper.getByOcrRecordId(ocrRecordId);
    }

}                                                                                                        
