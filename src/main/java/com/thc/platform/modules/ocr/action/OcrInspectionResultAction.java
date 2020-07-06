package com.thc.platform.modules.ocr.action;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.ocr.bean.OcrInspectionResult;
import com.thc.platform.modules.ocr.bean.req.OcrInspectionResultReq;
import com.thc.platform.modules.ocr.biz.IOcrInspectionResultService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author @date             @version
 * zouyu            2020-02-01         1.0.0
 * *******************************************************
 * @Description: 控制层
 * <p>
 * *******************************************************
 * @update
 */
@RestController
@RequestMapping("/ocr/ocrInspectionResult")
public class OcrInspectionResultAction {

    @Resource
    private IOcrInspectionResultService ocrInspectionResultService;


    /**
     * @param ocrInspectionResult 请求实体
     * @return
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/createOcrInspectionResult", method = RequestMethod.POST)
    public Object createOcrInspectionResult(@RequestBody OcrInspectionResultReq ocrInspectionResult) {
        ocrInspectionResultService.insertOcrInspectionResult(ocrInspectionResult);
        return Api.ok(ocrInspectionResult);
    }

    /**
     * @return
     * @Description: 通过实体 的主键ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrInspectionResult", method = RequestMethod.POST)
    public Object getOcrInspectionResultInfo(@RequestBody String requestBody) {
        String id = JSON.parseObject(requestBody).getString("id");
        if (StringUtils.isEmpty(id)) {
            return Api.error("ID为空");
        }
        OcrInspectionResult ocrInspectionResultInfo = ocrInspectionResultService.getOcrInspectionResultById(id);
        return Api.ok(ocrInspectionResultInfo);
    }


    /**
     * @param ocrInspectionResult 查询实体对象
     * @return
     * @Description: 动态条件查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrInspectionResults", method = RequestMethod.POST)
    public Object getOcrInspectionResultList(@RequestBody OcrInspectionResultReq ocrInspectionResult) {
        Map<String, Object> result = new HashMap<>();
        result.put("resultList", ocrInspectionResultService.getOcrInspectionResults(ocrInspectionResult));
        result.put("totalCount", ocrInspectionResultService.getOcrInspectionResultsNum(ocrInspectionResult));
        return Api.ok(result);
    }

    /**
     * @param ocrInspectionResult 实体信息
     * @return
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/updateOcrInspectionResult", method = RequestMethod.POST)
    public Object updateOcrInspectionResult(@RequestBody OcrInspectionResult ocrInspectionResult) {
        if (StringUtils.isEmpty(ocrInspectionResult.getId())) {
            return Api.error("ID为空");
        }
        ocrInspectionResultService.updateOcrInspectionResult(ocrInspectionResult);
        return Api.ok(ocrInspectionResult);
    }

    /**
     * @param ocrInspectionResult 实体信息
     * @return
     * @Description: 刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/deleteOcrInspectionResult", method = RequestMethod.POST)
    public Object deleteOcrInspectionResult(@RequestBody OcrInspectionResult ocrInspectionResult) {
        if (StringUtils.isEmpty(ocrInspectionResult.getId())) {
            return Api.error("ID为空");
        }
        ocrInspectionResultService.deleteOcrInspectionResultById(ocrInspectionResult.getId());
        return Api.ok(ocrInspectionResult);
    }
}