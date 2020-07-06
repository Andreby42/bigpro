package com.thc.platform.modules.ocr.bean.req;

import com.thc.platform.modules.ocr.bean.OcrExamineResult;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
public class OcrExamineResultReq extends OcrExamineResult {

    private Date startTime;

    private Date endTime;
}
