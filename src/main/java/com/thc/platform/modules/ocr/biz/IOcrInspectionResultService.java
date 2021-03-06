package com.thc.platform.modules.ocr.biz;

import com.thc.platform.modules.ocr.bean.OcrInspectionResult;
import com.thc.platform.modules.ocr.bean.req.OcrInspectionResultReq;

import java.util.List;

/**
 * @author @date             @version
 * zouyu            2020-02-01         1.0.0
 * *******************************************************
 * @Description: 业务接口层
 * <p>
 * *******************************************************
 * @update
 */
public interface IOcrInspectionResultService {

    /**
     * @param ocrInspectionResult 实体信息
     * @return
     * @Description: 插入数据
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int insertOcrInspectionResult(OcrInspectionResult ocrInspectionResult);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    OcrInspectionResult getOcrInspectionResultById(String id);

    /**
     * @param ocrInspectionResult
     * @return
     * @Description: 动态查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    List<OcrInspectionResult> getOcrInspectionResults(OcrInspectionResultReq ocrInspectionResult);

    /**
     * @param ocrInspectionResult 实体信息
     * @return
     * @Description: 通过ID更新
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int updateOcrInspectionResult(OcrInspectionResult ocrInspectionResult);

    /**
     * @param ocrInspectionResult 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int getOcrInspectionResultsNum(OcrInspectionResultReq ocrInspectionResult);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    void deleteOcrInspectionResultById(String id);

    OcrInspectionResult userModify(OcrInspectionResult ocrInspectionResult);

    List<OcrInspectionResult> getByOcrRecordIdIn(List<String> ocrRecordIds);

    List<OcrInspectionResult> getByOcrRecordId(String ocrRecordId);
}
