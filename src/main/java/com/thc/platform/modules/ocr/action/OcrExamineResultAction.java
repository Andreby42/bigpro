package com.thc.platform.modules.ocr.action;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.ocr.bean.OcrExamineResult;
import com.thc.platform.modules.ocr.bean.req.OcrExamineResultReq;
import com.thc.platform.modules.ocr.biz.IOcrExamineResultService;
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
@RequestMapping("/ocr/ocrExamineResult")
public class OcrExamineResultAction {

    @Resource
    private IOcrExamineResultService ocrExamineResultService;


    /**
     * @param ocrExamineResult 请求实体
     * @return
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/createOcrExamineResult", method = RequestMethod.POST)
    public Object createOcrExamineResult(@RequestBody OcrExamineResult ocrExamineResult) {
        ocrExamineResultService.insertOcrExamineResult(ocrExamineResult);
        return Api.ok(ocrExamineResult);
    }

    /**
     * @return
     * @Description: 通过实体 的主键ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrExamineResult", method = RequestMethod.POST)
    public Object getOcrExamineResultInfo(@RequestBody String requestBody) {
        String id = JSON.parseObject(requestBody).getString("id");
        if (StringUtils.isEmpty(id)) {
            return Api.error("ID为空");
        }
        OcrExamineResult ocrExamineResultInfo = ocrExamineResultService.getOcrExamineResultById(id);
        return Api.ok(ocrExamineResultInfo);
    }


    /**
     * @param ocrExamineResult 查询实体对象
     * @return
     * @Description: 动态条件查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrExamineResults", method = RequestMethod.POST)
    public Object getOcrExamineResultList(@RequestBody OcrExamineResultReq ocrExamineResult) {
        Map<String, Object> result = new HashMap<>();
        result.put("resultList", ocrExamineResultService.getOcrExamineResults(ocrExamineResult));
        result.put("totalCount", ocrExamineResultService.getOcrExamineResultsNum(ocrExamineResult));
        return Api.ok(result);
    }

    /**
     * @param ocrExamineResult 实体信息
     * @return
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/updateOcrExamineResult", method = RequestMethod.POST)
    public Object updateOcrExamineResult(@RequestBody OcrExamineResult ocrExamineResult) {
        if (StringUtils.isEmpty(ocrExamineResult.getId())) {
            return Api.error("ID为空");
        }
        ocrExamineResultService.updateOcrExamineResult(ocrExamineResult);
        return Api.ok(ocrExamineResult);
    }

    /**
     * @param ocrExamineResult 实体信息
     * @return
     * @Description: 刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/deleteOcrExamineResult", method = RequestMethod.POST)
    public Object deleteOcrExamineResult(@RequestBody OcrExamineResult ocrExamineResult) {
        if (StringUtils.isEmpty(ocrExamineResult.getId())) {
            return Api.error("ID为空");
        }
        ocrExamineResultService.deleteOcrExamineResultById(ocrExamineResult.getId());
        return Api.ok(ocrExamineResult);
    }
}