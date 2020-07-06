package com.thc.platform.modules.ocr.biz;

import com.thc.platform.modules.ocr.bean.OcrInspectionResultItem;

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
public interface IOcrInspectionResultItemService {

    /**
     * @param ocrInspectionResultItem 实体信息
     * @return
     * @Description: 插入数据
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int insertOcrInspectionResultItem(OcrInspectionResultItem ocrInspectionResultItem);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    OcrInspectionResultItem getOcrInspectionResultItemById(String id);

    /**
     * @param ocrInspectionResultItem
     * @return
     * @Description: 动态查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    List<OcrInspectionResultItem> getOcrInspectionResultItems(OcrInspectionResultItem ocrInspectionResultItem);

    /**
     * @param ocrInspectionResultItem 实体信息
     * @return
     * @Description: 通过ID更新
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int updateOcrInspectionResultItem(OcrInspectionResultItem ocrInspectionResultItem);

    /**
     * @param ocrInspectionResultItem 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int getOcrInspectionResultItemsNum(OcrInspectionResultItem ocrInspectionResultItem);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    void deleteOcrInspectionResultItemById(String id);


    List<OcrInspectionResultItem> batchInsert(List<OcrInspectionResultItem> ocrInspectionResultItems);

    List<OcrInspectionResultItem> batchUpdate(List<OcrInspectionResultItem> ocrInspectionResultItems);

    List<OcrInspectionResultItem> deleteAndModify(List<OcrInspectionResultItem> ocrInspectionResultItems);

    List<OcrInspectionResultItem> getByOcrInspectionResultId(String ocrInspectionResultId);

    List<OcrInspectionResultItem> getByOcrInspectionResultIdIn(List<String> ids);
}
