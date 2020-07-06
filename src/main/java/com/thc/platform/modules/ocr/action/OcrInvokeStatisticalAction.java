package com.thc.platform.modules.ocr.action;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.ocr.bean.OcrInvokeStatistical;
import com.thc.platform.modules.ocr.biz.IOcrInvokeStatisticalService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
@RequestMapping("/ocr/ocrInvokeStatistical")
public class OcrInvokeStatisticalAction {

    @Resource
    private IOcrInvokeStatisticalService ocrInvokeStatisticalService;


    /**
     * @param ocrInvokeStatistical 请求实体
     * @return
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/createOcrInvokeStatistical", method = RequestMethod.POST)
    public Object createOcrInvokeStatistical(@RequestBody OcrInvokeStatistical ocrInvokeStatistical) {
        ocrInvokeStatisticalService.insertOcrInvokeStatistical(ocrInvokeStatistical);
        return Api.ok(ocrInvokeStatistical);
    }

    /**
     * @return
     * @Description: 通过实体 的主键ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrInvokeStatistical", method = RequestMethod.POST)
    public Object getOcrInvokeStatisticalInfo(@RequestBody String requestBody) {
        String id = JSON.parseObject(requestBody).getString("id");
        if (StringUtils.isEmpty(id)) {
            return Api.error("ID为空");
        }
        OcrInvokeStatistical ocrInvokeStatisticalInfo = ocrInvokeStatisticalService.getOcrInvokeStatisticalById(id);
        return Api.ok(ocrInvokeStatisticalInfo);
    }


    /**
     * @param ocrInvokeStatistical 查询实体对象
     * @return
     * @Description: 动态条件查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrInvokeStatisticals", method = RequestMethod.POST)
    public Object getOcrInvokeStatisticalList(@RequestBody OcrInvokeStatistical ocrInvokeStatistical) {
        return Api.ok(ocrInvokeStatisticalService.getOcrInvokeStatisticals(ocrInvokeStatistical), +ocrInvokeStatisticalService.getOcrInvokeStatisticalsNum(ocrInvokeStatistical));
    }

    /**
     * @param ocrInvokeStatistical 实体信息
     * @return
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/updateOcrInvokeStatistical", method = RequestMethod.POST)
    public Object updateOcrInvokeStatistical(@RequestBody OcrInvokeStatistical ocrInvokeStatistical) {
        if (StringUtils.isEmpty(ocrInvokeStatistical.getId())) {
            return Api.error("ID为空");
        }
        ocrInvokeStatisticalService.updateOcrInvokeStatistical(ocrInvokeStatistical);
        return Api.ok(ocrInvokeStatistical);
    }

    /**
     * @param ocrInvokeStatistical 实体信息
     * @return
     * @Description: 刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/deleteOcrInvokeStatistical", method = RequestMethod.POST)
    public Object deleteOcrInvokeStatistical(@RequestBody OcrInvokeStatistical ocrInvokeStatistical) {
        if (StringUtils.isEmpty(ocrInvokeStatistical.getId())) {
            return Api.error("ID为空");
        }
        ocrInvokeStatisticalService.deleteOcrInvokeStatisticalById(ocrInvokeStatistical.getId());
        return Api.ok(ocrInvokeStatistical);
    }
}