package com.thc.platform.modules.ocr.util.factory;

import com.google.common.collect.Lists;
import com.thc.platform.modules.ocr.bean.OcrExamineResult;
import com.thc.platform.modules.ocr.bean.OcrInspectionResult;
import com.thc.platform.modules.ocr.bean.OcrInspectionResultItem;
import com.thc.platform.modules.ocr.bean.OcrRecord;
import com.thc.platform.modules.ocr.bean.baidu.OcrResultDO;
import com.thc.platform.modules.ocr.bean.baidu.OcrResultDataDO;
import com.thc.platform.modules.ocr.util.BaiDuRecognitionUtils;
import com.thc.platform.modules.ocr.util.DateTimeUtil;
import com.thc.platform.modules.ocr.util.constant.OcrConstants;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * 百度识别工具
 */
@Component
public class BaiduRecognition extends AbstractRecognition<OcrResultDO> {

    @Resource
    private BaiDuRecognitionUtils baiDuRecognitionUtils;

    // 双列left+right 单列left
    private static final String LEFT_COLUMN_RESULT = "leftTable";
    private static final String RIGHT_COLUMN_RESULT = "rightTable";


    @Override
    protected OcrResultDO doRecognition(OcrRecord ocrRecord) {
        return baiDuRecognitionUtils.recognition(ocrRecord.getLocalFilePath(), ocrRecord.getType(), ocrRecord.getToken());

    }

    @Override
    protected OcrRecord transfer(OcrRecord ocrRecord, OcrResultDO result) {
        if (result != null) {
            String error_code = result.getError_code();
            if ("0".equals(error_code)) {
                ocrRecord.setResult(OcrConstants.Result.SUCCESS);
                Integer type = ocrRecord.getType();
                if (OcrConstants.ImgType.EXAMINE.equals(type)) {
                    this.transferExamine(ocrRecord, result);
                } else if (OcrConstants.ImgType.INSPECTION.equals(type)) {
                    this.transferInspection(ocrRecord, result);
                } else {
                    throw new RuntimeException("图片类型不符合");
                }
            } else {
                ocrRecord.setErrorMsg(result.getError_msg());
                ocrRecord.setResult(OcrConstants.Result.FAIL);
            }
            return ocrRecord;
        }
        throw new RuntimeException("识别失败,结果为空");
    }

    /**
     * todo
     *
     * @param ocrRecord
     * @param result
     * @return
     */
    private OcrRecord transferInspection(OcrRecord ocrRecord, OcrResultDO result) {
        OcrResultDataDO data = result.getData();
        ocrRecord.setTemplateSign(data.getTemplateSign());
        ocrRecord.setTemplateName(data.getTemplateName());
        ocrRecord.setScore(data.getScores());
        List<OcrResultDataDO.OcrResultDataKVDo> ret = data.getRet();
        OcrInspectionResult ocrInspectionResult = new OcrInspectionResult();
        List<OcrInspectionResultItem> ocrInspectionResultItems;
        ret.forEach(r -> {
            String wordName = r.getWord_name();
            try {
                Field declaredField = ocrInspectionResult.getClass().getDeclaredField(wordName);
                declaredField.setAccessible(true);
                String word = r.getWord();
                // 检验时间转换
                if (OcrConstants.INSPECTION_DATE_TYPE_FIELD.contains(wordName)) {
                    word = DateTimeUtil.dateStrFormat(word);
                }
                declaredField.set(ocrInspectionResult, word);
                declaredField.setAccessible(false);
            } catch (NoSuchFieldException e) {
                //
            } catch (IllegalAccessException e) {
                throw new RuntimeException("OCR结构转换失败");
            }
        });
        ocrInspectionResultItems = this.transferInspectionItem(ret);
        ocrInspectionResult.setOcrInspectionResultItems(ocrInspectionResultItems);
        ocrRecord.setOcrInspectionResult(ocrInspectionResult);
        return ocrRecord;
    }

    private List<OcrInspectionResultItem> transferInspectionItem(List<OcrResultDataDO.OcrResultDataKVDo> ret) {
        List<OcrInspectionResultItem> ocrInspectionResultItems = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(ret)) {
            List<OcrResultDataDO.OcrResultDataKVDo> left = ret.stream().filter(r -> r.getWord_name().contains(LEFT_COLUMN_RESULT)).collect(Collectors.toList());
            List<OcrResultDataDO.OcrResultDataKVDo> right = ret.stream().filter(r -> r.getWord_name().contains(RIGHT_COLUMN_RESULT)).collect(Collectors.toList());
            List<OcrInspectionResultItem> leftResult = this.resolveTable(left);
            List<OcrInspectionResultItem> rightResult = this.resolveTable(right);
            if (CollectionUtils.isNotEmpty(leftResult)) ocrInspectionResultItems.addAll(leftResult);
            if (CollectionUtils.isNotEmpty(rightResult)) ocrInspectionResultItems.addAll(rightResult);
            // 添加系统序号
            for (int i = 0; i < ocrInspectionResultItems.size(); i++) {
                OcrInspectionResultItem item = ocrInspectionResultItems.get(i);
                item.setNo(i + 1 + "");
            }
        }
        return ocrInspectionResultItems;
    }

    private List<OcrInspectionResultItem> resolveTable(List<OcrResultDataDO.OcrResultDataKVDo> table) {
        if (CollectionUtils.isNotEmpty(table)) {
            Map<Integer, OcrInspectionResultItem> ocrInspectionResultItemMap = new TreeMap<>();
            for (OcrResultDataDO.OcrResultDataKVDo item : table) {
                String wordName = item.getWord_name();
                String[] split = wordName.split("#");
                Integer key = Integer.valueOf(split[1]);
                String field = split[2];
                OcrInspectionResultItem ocrInspectionResultItem = ocrInspectionResultItemMap.get(key);
                if (null == ocrInspectionResultItem) {
                    ocrInspectionResultItem = new OcrInspectionResultItem();
                    ocrInspectionResultItemMap.put(key, ocrInspectionResultItem);
                }
                try {
                    Field declaredField = ocrInspectionResultItem.getClass().getDeclaredField(field);
                    declaredField.setAccessible(true);
                    String value = item.getWord();
                    // 处理检验单上单箭头
                    if (value.contains("↓")) {
                        declaredField.set(ocrInspectionResultItem, value.replaceAll("↓", ""));
                        ocrInspectionResultItem.setStatus(OcrConstants.InspectionResultStatus.LOW);
                    } else if (value.contains("↑")) {
                        declaredField.set(ocrInspectionResultItem, value.replaceAll("↑", ""));
                        ocrInspectionResultItem.setStatus(OcrConstants.InspectionResultStatus.HIGH);
                    } else {
                        declaredField.set(ocrInspectionResultItem, value);
                    }
                    declaredField.setAccessible(false);
                } catch (NoSuchFieldException | IllegalAccessException e) {
//                    throw new RuntimeException("对象解析失败", e);
                }
            }
            if (MapUtils.isNotEmpty(ocrInspectionResultItemMap)) {
                return Lists.newArrayList(ocrInspectionResultItemMap.values());
            }
        }
        return null;
    }

    private OcrRecord transferExamine(OcrRecord ocrRecord, OcrResultDO result) {
        OcrResultDataDO data = result.getData();
        ocrRecord.setTemplateSign(data.getTemplateSign());
        ocrRecord.setTemplateName(data.getTemplateName());
        ocrRecord.setScore(data.getScores());
        List<OcrResultDataDO.OcrResultDataKVDo> ret = data.getRet();
        OcrExamineResult ocrExamineResult = new OcrExamineResult();
        ret.forEach(r -> {
            String wordName = r.getWord_name();
            try {
                Field declaredField = ocrExamineResult.getClass().getDeclaredField(wordName);
                declaredField.setAccessible(true);
                String word = r.getWord();
                // 检查时间转换
                if (OcrConstants.EXAMINE_DATE_TYPE_FIELD.contains(wordName)) {
                    word = DateTimeUtil.dateStrFormat(word);
                }
                declaredField.set(ocrExamineResult, word);
                declaredField.setAccessible(false);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                //
            }
        });
        ocrRecord.setOcrExamineResult(ocrExamineResult);
        return ocrRecord;
    }

//    public static void main(String[] args) throws Exception {
//        byte[] bytes = Files.readAllBytes(Paths.get("/Users/zouyu/Desktop/ocr-result.json"));
//        String s = new String(bytes);
//        OcrResultDO ocrResultDO = JSON.parseObject(s, OcrResultDO.class);
//        OcrRecord ocrRecord = new OcrRecord();
//        ocrRecord.setType(1);
//        BaiduRecognition baiduRecognition = new BaiduRecognition();
//        baiduRecognition.transfer(ocrRecord, ocrResultDO);
//        System.out.println(JSON.toJSON(ocrRecord));
//    }
}
