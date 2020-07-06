package com.thc.platform.modules.ocr.dao;

import com.thc.platform.modules.ocr.bean.OcrExamineResult;
import com.thc.platform.modules.ocr.bean.req.OcrExamineResultReq;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author @date             @version
 * zouyu            2020-02-01         1.0.0
 * *******************************************************
 * @Description: 数据接口层
 * <p>
 * *******************************************************
 * @update
 */
public interface OcrExamineResultMapper {

    /**
     * @param ocrExamineResult 实体信息
     * @return
     * @Description: 插入数据
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int insertOcrExamineResult(OcrExamineResult ocrExamineResult);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    OcrExamineResult getOcrExamineResultById(String id);

    /**
     * @param ocrExamineResult
     * @return
     * @Description: 动态查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    List<OcrExamineResult> getOcrExamineResults(OcrExamineResultReq ocrExamineResult);

    /**
     * @param ocrExamineResult 实体信息
     * @return
     * @Description: 通过ID更新
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int updateOcrExamineResult(OcrExamineResult ocrExamineResult);

    /**
     * @param ocrExamineResult 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int getOcrExamineResultsNum(OcrExamineResultReq ocrExamineResult);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    void deleteOcrExamineResultById(String id);

    List<OcrExamineResult> getByOcrRecordIdIn(@Param("ocrRecordIds") List<String> ocrRecordIds);

    List<OcrExamineResult> getByOcrRecordId(String ocrRecordId);
}
