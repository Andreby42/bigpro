package com.thc.platform.modules.ocr.action;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.ocr.bean.OcrInspectionResultItem;
import com.thc.platform.modules.ocr.biz.IOcrInspectionResultItemService;
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
@RequestMapping("/ocr/ocrInspectionResultItem")
public class OcrInspectionResultItemAction {

    @Resource
    private IOcrInspectionResultItemService ocrInspectionResultItemService;


    /**
     * @param ocrInspectionResultItem 请求实体
     * @return
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/createOcrInspectionResultItem", method = RequestMethod.POST)
    public Object createOcrInspectionResultItem(@RequestBody OcrInspectionResultItem ocrInspectionResultItem) {
        ocrInspectionResultItemService.insertOcrInspectionResultItem(ocrInspectionResultItem);
        return Api.ok(ocrInspectionResultItem);
    }

    /**
     * @return
     * @Description: 通过实体 的主键ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrInspectionResultItem", method = RequestMethod.POST)
    public Object getOcrInspectionResultItemInfo(@RequestBody String requestBody) {
        String id = JSON.parseObject(requestBody).getString("id");
        if (StringUtils.isEmpty(id)) {
            return Api.error("ID为空");
        }
        OcrInspectionResultItem ocrInspectionResultItemInfo = ocrInspectionResultItemService.getOcrInspectionResultItemById(id);
        return Api.ok(ocrInspectionResultItemInfo);
    }


    /**
     * @param ocrInspectionResultItem 查询实体对象
     * @return
     * @Description: 动态条件查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/getOcrInspectionResultItems", method = RequestMethod.POST)
    public Object getOcrInspectionResultItemList(@RequestBody OcrInspectionResultItem ocrInspectionResultItem) {
        return Api.ok(ocrInspectionResultItemService.getOcrInspectionResultItems(ocrInspectionResultItem), +ocrInspectionResultItemService.getOcrInspectionResultItemsNum(ocrInspectionResultItem));
    }

    /**
     * @param ocrInspectionResultItem 实体信息
     * @return
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/updateOcrInspectionResultItem", method = RequestMethod.POST)
    public Object updateOcrInspectionResultItem(@RequestBody OcrInspectionResultItem ocrInspectionResultItem) {
        if (StringUtils.isEmpty(ocrInspectionResultItem.getId())) {
            return Api.error("ID为空");
        }
        ocrInspectionResultItemService.updateOcrInspectionResultItem(ocrInspectionResultItem);
        return Api.ok(ocrInspectionResultItem);
    }

    /**
     * @param ocrInspectionResultItem 实体信息
     * @return
     * @Description: 刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @RequestMapping(value = "/deleteOcrInspectionResultItem", method = RequestMethod.POST)
    public Object deleteOcrInspectionResultItem(@RequestBody OcrInspectionResultItem ocrInspectionResultItem) {
        if (StringUtils.isEmpty(ocrInspectionResultItem.getId())) {
            return Api.error("ID为空");
        }
        ocrInspectionResultItemService.deleteOcrInspectionResultItemById(ocrInspectionResultItem.getId());
        return Api.ok(ocrInspectionResultItem);
    }
}