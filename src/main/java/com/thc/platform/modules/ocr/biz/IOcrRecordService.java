package com.thc.platform.modules.ocr.biz;

import com.thc.platform.modules.ocr.bean.OcrInvokeStatistical;
import com.thc.platform.modules.ocr.bean.OcrRecord;

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
public interface IOcrRecordService {

    /**
     * @param ocrRecord 实体信息
     * @return
     * @Description: 插入数据
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int insertOcrRecord(OcrRecord ocrRecord);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    OcrRecord getOcrRecordById(String id);

    /**
     * @param ocrRecord
     * @return
     * @Description: 动态查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    List<OcrRecord> getOcrRecords(OcrRecord ocrRecord);

    /**
     * @param ocrRecord 实体信息
     * @return
     * @Description: 通过ID更新
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int updateOcrRecord(OcrRecord ocrRecord);

    /**
     * @param ocrRecord 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    int getOcrRecordsNum(OcrRecord ocrRecord);

    /**
     * @param id UUID
     * @return
     * @Description: 通过ID刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    void deleteOcrRecordById(String id);

    /**
     * 识别单据
     *
     * @param ocrRecord
     * @return
     */
    OcrRecord recognition(OcrRecord ocrRecord);

    OcrRecord modifyResult(OcrRecord ocrRecord);

    List<OcrRecord> queryUserResult(OcrRecord ocrRecord);

    List<OcrRecord> getUserUploadRecord(OcrRecord ocrRecord);

    Object getResultById(String id);

    List<OcrInvokeStatistical> getCountByTenantIdAndOrgId(String stDay);
}
