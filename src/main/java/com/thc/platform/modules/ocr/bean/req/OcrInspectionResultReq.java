package com.thc.platform.modules.ocr.bean.req;

import com.thc.platform.modules.ocr.bean.OcrInspectionResult;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class OcrInspectionResultReq extends OcrInspectionResult {

    private Date startTime;

    private Date endTime;
}
