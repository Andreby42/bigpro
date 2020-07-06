package com.thc.platform.modules.ocr.util.checker;

import com.thc.platform.modules.ocr.bean.OcrRecord;
import org.springframework.stereotype.Component;

@Component
public class OcrRecordChecker extends BeanChecker {

    public void checkRecognition(OcrRecord ocrRecord) {
        assertNotNull(ocrRecord, "参数为空");
        assertNotEmpty(ocrRecord.getTenantId(), "租户ID为空");
        assertNotEmpty(ocrRecord.getTenantName(), "租户名称为空");
        assertNotEmpty(ocrRecord.getOrgId(), "机构ID为空");
        assertNotEmpty(ocrRecord.getOrgName(), "机构名称为空");
        assertNotEmpty(ocrRecord.getUrl(), "图片地址为空");
        assertNotNull(ocrRecord.getSourceType(), "来源类型为空");
        assertNotNull(ocrRecord.getType(), "图片类型为空");
        assertNotNull(ocrRecord.getOperatorType(), "操作人员类型");
        assertNotEmpty(ocrRecord.getOperatorName(), "操作人姓名姓名为空");
        assertNotEmpty(ocrRecord.getOperatorId(), "操作人ID为空");
        assertNotEmpty(ocrRecord.getFileId(), "文件存储ID为空");
    }

    public void checkModifyResult(OcrRecord ocrRecord) {
        assertNotNull(ocrRecord, "参数为空");
        assertNotEmpty(ocrRecord.getId(), "ID为空");
    }

    public void checkMergeQuery(OcrRecord ocrRecord) {
        assertNotNull(ocrRecord, "参数为空");
        assertNotEmpty(ocrRecord.getTenantId(), "租户ID为空");
        assertNotEmpty(ocrRecord.getOrgId(), "机构ID为空");
        assertNotEmpty(ocrRecord.getOperatorId(), "操作人ID为空");
    }
}
