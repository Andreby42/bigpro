package com.thc.platform.modules.ocr.action;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.ocr.bean.OcrRecord;
import com.thc.platform.modules.ocr.biz.IOcrRecordService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/ocr/ocrRecord")
public class OcrRecordAction {

    @Resource
    private IOcrRecordService ocrRecordService;


    /**
     * @param ocrRecord 请求实体
     * @return
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/createOcrRecord", method = RequestMethod.POST)
    public Object createOcrRecord(@RequestBody OcrRecord ocrRecord) {
        ocrRecordService.insertOcrRecord(ocrRecord);
        return Api.ok(ocrRecord);
    }

    /**
     * @param id 请求id
     * @return
     * @Description: 通过实体 的主键ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrRecord", method = RequestMethod.POST)
    public Object getOcrRecordInfo(@RequestBody String requestBody) {
        String id = JSON.parseObject(requestBody).getString("id");
        if (StringUtils.isEmpty(id)) {
            return Api.error("ID为空");
        }
        OcrRecord ocrRecordInfo = ocrRecordService.getOcrRecordById(id);
        return Api.ok(ocrRecordInfo);
    }


    /**
     * @param ocrRecord 查询实体对象
     * @return
     * @Description: 动态条件查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrRecords", method = RequestMethod.POST)
    public Object getOcrRecordList(@RequestBody OcrRecord ocrRecord) {
        Map<String, Object> result = new HashMap<>();
        result.put("resultList", ocrRecordService.getOcrRecords(ocrRecord));
        result.put("totalCount", ocrRecordService.getOcrRecordsNum(ocrRecord));
        return Api.ok(result);
    }

    /**
     * @param ocrRecord 实体信息
     * @return
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/updateOcrRecord", method = RequestMethod.POST)
    public Object updateOcrRecord(@RequestBody OcrRecord ocrRecord) {
        if (StringUtils.isEmpty(ocrRecord.getId())) {
            return Api.error("ID为空");
        }
        ocrRecordService.updateOcrRecord(ocrRecord);
        return Api.ok(ocrRecord);
    }

    /**
     * @param ocrRecord 实体信息
     * @return
     * @Description: 刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/deleteOcrRecord", method = RequestMethod.POST)
    public Object deleteOcrRecord(@RequestBody OcrRecord ocrRecord) {
        if (StringUtils.isEmpty(ocrRecord.getId())) {
            return Api.error("ID为空");
        }
        ocrRecordService.deleteOcrRecordById(ocrRecord.getId());
        return Api.ok(ocrRecord);
    }


    /**
     * 识别单据接口
     *
     * @param ocrRecord
     * @return
     */
    @PostMapping("/recognition")
    public Object recognition(@RequestBody OcrRecord ocrRecord) {
        return Api.ok(ocrRecordService.recognition(ocrRecord));
    }

    /**
     * 用户创建或修改识别结果
     *
     * @param ocrRecord
     * @return
     */
    @PostMapping("/modifyResult")
    public Object modifyResult(@RequestBody OcrRecord ocrRecord) {
        return Api.ok(ocrRecordService.modifyResult(ocrRecord));
    }


    /**
     * 合并查询用户上传的报告+结果
     *
     * @param ocrRecord
     * @return
     */
    @PostMapping("/queryUserResult")
    public Object queryUserResult(@RequestBody OcrRecord ocrRecord) {
        return Api.ok(ocrRecordService.queryUserResult(ocrRecord));
    }

    @PostMapping("/getResultById")
    public Object getResultById(@RequestBody OcrRecord ocrRecord) {
        return Api.ok(ocrRecordService.getResultById(ocrRecord.getId()));
    }

}