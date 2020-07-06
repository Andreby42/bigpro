package com.thc.platform.modules.ocr.dao;

import com.thc.platform.modules.ocr.bean.OcrInvokeStatistical;

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
public interface OcrInvokeStatisticalMapper {

    /**
     * @param ocrInvokeStatistical 实体信息
     * @return
     * @Description: 插入数据
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int insertOcrInvokeStatistical(OcrInvokeStatistical ocrInvokeStatistical);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    OcrInvokeStatistical getOcrInvokeStatisticalById(String id);

    /**
     * @param ocrInvokeStatistical
     * @return
     * @Description: 动态查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    List<OcrInvokeStatistical> getOcrInvokeStatisticals(OcrInvokeStatistical ocrInvokeStatistical);

    /**
     * @param ocrInvokeStatistical 实体信息
     * @return
     * @Description: 通过ID更新
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int updateOcrInvokeStatistical(OcrInvokeStatistical ocrInvokeStatistical);

    /**
     * @param ocrInvokeStatistical 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int getOcrInvokeStatisticalsNum(OcrInvokeStatistical ocrInvokeStatistical);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    void deleteOcrInvokeStatisticalById(String id);

}                                                                             
