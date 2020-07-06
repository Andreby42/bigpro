package com.thc.platform.modules.ocr.biz.impl;

import com.thc.platform.modules.ocr.bean.*;
import com.thc.platform.modules.ocr.biz.IOcrExamineResultService;
import com.thc.platform.modules.ocr.biz.IOcrInspectionResultItemService;
import com.thc.platform.modules.ocr.biz.IOcrInspectionResultService;
import com.thc.platform.modules.ocr.biz.IOcrRecordService;
import com.thc.platform.modules.ocr.dao.OcrRecordMapper;
import com.thc.platform.modules.ocr.util.DateTimeUtil;
import com.thc.platform.modules.ocr.util.checker.OcrRecordChecker;
import com.thc.platform.modules.ocr.util.constant.OcrConstants;
import com.thc.platform.modules.ocr.util.factory.RecognitionFactory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author @date             @version
 * zouyu            2020-02-01         1.0.0
 * *******************************************************
 * @Description: 服务实现层
 * <p>
 * *******************************************************
 * @update
 */
@Service
public class OcrRecordServiceImpl implements IOcrRecordService {

    @Resource
    private OcrRecordMapper ocrRecordMapper;
    @Resource
    private OcrRecordChecker checker;
    @Resource
    private IOcrInspectionResultService ocrInspectionResultService;
    @Resource
    private IOcrInspectionResultItemService ocrInspectionResultItemService;
    @Resource
    private IOcrExamineResultService ocrExamineResultService;

    /**
     * @param ocrRecord 实体信息
     * @return 成功条数
     * @Description: 创建实体
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public int insertOcrRecord(OcrRecord ocrRecord) {
        return ocrRecordMapper.insertOcrRecord(ocrRecord);
    }

    /**
     * @param id 主键ID
     * @return 实体信息
     * @Description: 通过ID查询实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public OcrRecord getOcrRecordById(String id) {
        return ocrRecordMapper.getOcrRecordById(id);
    }

    /**
     * @param ocrRecord 查询条件
     * @return 实体信息集合
     * @Description: 获取实体信息集合
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public List<OcrRecord> getOcrRecords(OcrRecord ocrRecord) {
        this.buildQuery(ocrRecord);
        return ocrRecordMapper.getOcrRecords(ocrRecord);
    }

    /**
     * @param ocrRecord 实体信息
     * @return 更新条数
     * @Description: 更新实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public int updateOcrRecord(OcrRecord ocrRecord) {
        return ocrRecordMapper.updateOcrRecord(ocrRecord);
    }

    /**
     * @param ocrRecord 实体信息
     * @return
     * @Description: 动态查询数据总条数
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    public int getOcrRecordsNum(OcrRecord ocrRecord) {
        this.buildQuery(ocrRecord);
        return ocrRecordMapper.getOcrRecordsNum(ocrRecord);
    }

    /**
     * @param id 主键ID
     * @return 实体信息
     * @Description: 通过ID刪除实体信息
     * @author: zouyu
     * @date: 2020-02-01
     * @version: 1.0.0
     */
    @Override
    public void deleteOcrRecordById(String id) {
        ocrRecordMapper.deleteOcrRecordById(id);
    }

    @Override
    @Transactional
    public OcrRecord recognition(OcrRecord ocrRecord) {
        // 必填项校验
        checker.checkRecognition(ocrRecord);
        // 获取token
        // 填充ocr识别记录对象
        this.fillOcrRecord(ocrRecord);
        // 识别图像
        ocrRecord = RecognitionFactory.createRecognition().recognition(ocrRecord);
        // 保存记录表
        this.insertOcrRecord(ocrRecord);
        // 如果识别成功，保存到检查表或者检验表
        if (OcrConstants.Result.SUCCESS.equals(ocrRecord.getResult())) {
            Integer type = ocrRecord.getType();
            if (OcrConstants.ImgType.INSPECTION.equals(type)) {
                OcrInspectionResult ocrInspectionResult = ocrRecord.getOcrInspectionResult();
                this.fillOcrInspectionResult(ocrInspectionResult, ocrRecord);
                ocrInspectionResultService.insertOcrInspectionResult(ocrInspectionResult);
                List<OcrInspectionResultItem> ocrInspectionResultItems = ocrInspectionResult.getOcrInspectionResultItems();
                this.fillOcrInspectionResultItem(ocrInspectionResultItems, ocrInspectionResult);
                ocrInspectionResultItemService.batchInsert(ocrInspectionResultItems);
            } else if (OcrConstants.ImgType.EXAMINE.equals(type)) {
                OcrExamineResult ocrExamineResult = ocrRecord.getOcrExamineResult();
                this.fillOcrExamineResult(ocrExamineResult, ocrRecord);
                ocrExamineResultService.insertOcrExamineResult(ocrExamineResult);
            }
        }
        return ocrRecord;
    }

    @Override
    @Transactional
    public OcrRecord modifyResult(OcrRecord ocrRecord) {
        checker.checkModifyResult(ocrRecord);
        String id = ocrRecord.getId();
        // 获取当前ocr识别记录
        OcrRecord currentRecord = this.getOcrRecordById(id);
        if (currentRecord == null) throw new RuntimeException("识别记录为空");
        // 获取单据类型
        Integer type = currentRecord.getType();
        // 如果检验单
        if (OcrConstants.ImgType.INSPECTION.equals(type)) {
            // 获取修改项
            OcrInspectionResult ocrInspectionResult = ocrRecord.getOcrInspectionResult();
            if (null == ocrInspectionResult) throw new RuntimeException("检验结果为空");
            // 保存检验结果
            ocrInspectionResult.setOcrRecordId(id);
            ocrInspectionResult.setResultType(OcrConstants.ResultType.MANUAL);
            ocrInspectionResult = ocrInspectionResultService.userModify(ocrInspectionResult);
            String ocrInspectionResultId = ocrInspectionResult.getId();

            // 保存检验结果明细
            List<OcrInspectionResultItem> ocrInspectionResultItems = ocrInspectionResult.getOcrInspectionResultItems();
            if (CollectionUtils.isEmpty(ocrInspectionResultItems)) throw new RuntimeException("检验结果明细为空");
            ocrInspectionResultItems.forEach(item ->
                    item.setOcrInspectionResultId(ocrInspectionResultId)
            );
            ocrInspectionResultItems = ocrInspectionResultItemService.deleteAndModify(ocrInspectionResultItems);
            ocrInspectionResult.setOcrInspectionResultItems(ocrInspectionResultItems);
            currentRecord.setOcrInspectionResult(ocrInspectionResult);
        }
        // 检查
        else if (OcrConstants.ImgType.EXAMINE.equals(type)) {
            OcrExamineResult ocrExamineResult = ocrRecord.getOcrExamineResult();
            if (null == ocrExamineResult) throw new RuntimeException("检查结果为空");
            ocrExamineResult.setOcrRecordId(id);
            ocrExamineResult.setResultType(OcrConstants.ResultType.MANUAL);
            ocrExamineResult = ocrExamineResultService.userModify(ocrExamineResult);
            currentRecord.setOcrExamineResult(ocrExamineResult);
        } else {
            throw new RuntimeException("类型错误");
        }
        return currentRecord;
    }

    @Override
    public List<OcrRecord> queryUserResult(OcrRecord ocrRecord) {
        checker.checkMergeQuery(ocrRecord);
        // 查询用户上传有效结果（互联网医院视用户没有手动点保存并上传为无效数据）
        List<OcrRecord> search = this.getUserUploadRecord(ocrRecord);
        if (CollectionUtils.isNotEmpty(search)) {
            List<String> examineRecordIdList = search
                    .stream()
                    .filter(r -> OcrConstants.ImgType.EXAMINE.equals(r.getType()))
                    .map(OcrRecord::getId)
                    .collect(Collectors.toList());

            List<String> inspectionRecordIdList = search
                    .stream()
                    .filter(r -> OcrConstants.ImgType.INSPECTION.equals(r.getType()))
                    .map(OcrRecord::getId)
                    .collect(Collectors.toList());

            List<OcrInspectionResult> ocrInspectionResults =
                    ocrInspectionResultService.getByOcrRecordIdIn(inspectionRecordIdList)
                            .stream()
                            .filter(r -> OcrConstants.ResultType.MANUAL.equals(r.getResultType()))
                            .collect(Collectors.toList());

            Map<String, List<OcrInspectionResult>> inspectionResultMap = ocrInspectionResults
                    .stream()
                    .collect(Collectors.groupingBy(OcrInspectionResult::getOcrRecordId));

            List<OcrExamineResult> ocrExamineResults =
                    ocrExamineResultService.getByOcrRecordIdIn(examineRecordIdList)
                            .stream()
                            .filter(r -> OcrConstants.ResultType.MANUAL.equals(r.getResultType()))
                            .collect(Collectors.toList());

            Map<String, List<OcrExamineResult>> examineResultMap = ocrExamineResults
                    .stream()
                    .collect(Collectors.groupingBy(OcrExamineResult::getOcrRecordId));
            // 填充结果
            search.forEach(item -> {
                String id = item.getId();
                List<OcrInspectionResult> resultList = inspectionResultMap.get(id);
                if (CollectionUtils.isNotEmpty(resultList)) {
                    item.setOcrInspectionResult(resultList.get(0));
                }

                List<OcrExamineResult> examineResultList = examineResultMap.get(id);
                if (CollectionUtils.isNotEmpty(examineResultList)) {
                    item.setOcrExamineResult(examineResultList.get(0));
                }

            });
        }
        return search;
    }

    @Override
    public List<OcrRecord> getUserUploadRecord(OcrRecord ocrRecord) {
        return ocrRecordMapper.getUserUploadRecord(ocrRecord);
    }

    @Override
    public Object getResultById(String id) {
        OcrRecord ocrRecord = this.getOcrRecordById(id);
        if (ocrRecord == null) throw new RuntimeException("结果不存在");
        Integer type = ocrRecord.getType();
        if (OcrConstants.ImgType.INSPECTION.equals(type)) {
            return ocrInspectionResultService.getByOcrRecordId(id);
        } else if (OcrConstants.ImgType.EXAMINE.equals(type)) {
            return ocrExamineResultService.getByOcrRecordId(id);
        }
        return null;
    }

    @Override
    public List<OcrInvokeStatistical> getCountByTenantIdAndOrgId(String stDay) {
        return ocrRecordMapper.getCountByTenantIdAndOrgId(stDay);
    }

    /**
     * 填充检验结果
     *
     * @param ocrInspectionResult
     * @param ocrRecord
     */
    private void fillOcrInspectionResult(OcrInspectionResult ocrInspectionResult, OcrRecord ocrRecord) {
        if (null == ocrInspectionResult) {
            throw new RuntimeException("识别检验结果为空");
        }
        // 保存到检验结果表
        ocrInspectionResult.setId(UUID.randomUUID().toString());
        ocrInspectionResult.setOcrRecordId(ocrRecord.getId());
        ocrInspectionResult.setTenantId(ocrRecord.getTenantId());
        ocrInspectionResult.setOrgId(ocrRecord.getOrgId());
        ocrInspectionResult.setResultType(OcrConstants.ResultType.BAI_DU);
        ocrInspectionResult.setPatientId(ocrRecord.getPatientId());
        ocrInspectionResult.setCreator(ocrRecord.getCreator());
    }

    /**
     * 填充检验明细
     *
     * @param ocrInspectionResultItems
     * @param ocrInspectionResult
     */
    private void fillOcrInspectionResultItem(List<OcrInspectionResultItem> ocrInspectionResultItems, OcrInspectionResult ocrInspectionResult) {
        if (CollectionUtils.isEmpty(ocrInspectionResultItems)) {
            throw new RuntimeException("识别检验结果明细为空");
        }
        for (int i = 0; i < ocrInspectionResultItems.size(); i++) {
            OcrInspectionResultItem item = ocrInspectionResultItems.get(i);
            item.setId(UUID.randomUUID().toString());
            item.setOcrInspectionResultId(ocrInspectionResult.getId());
            item.setTenantId(ocrInspectionResult.getTenantId());
            item.setOrgId(ocrInspectionResult.getOrgId());
            if (StringUtils.isEmpty(item.getNo())) {
                item.setNo(String.valueOf(i));
            }
            item.setCreator(ocrInspectionResult.getCreator());
        }
    }

    private void fillOcrExamineResult(OcrExamineResult ocrExamineResult, OcrRecord ocrRecord) {
        if (null == ocrExamineResult) {
            throw new RuntimeException("识别检查结果明细为空");
        }
        ocrExamineResult.setId(UUID.randomUUID().toString());
        ocrExamineResult.setOcrRecordId(ocrRecord.getId());
        ocrExamineResult.setTenantId(ocrRecord.getTenantId());
        ocrExamineResult.setOrgId(ocrRecord.getOrgId());
        ocrExamineResult.setResultType(OcrConstants.ResultType.BAI_DU);
        ocrExamineResult.setPatientId(ocrRecord.getPatientId());
        ocrExamineResult.setCreator(ocrRecord.getCreator());
    }

    /**
     * 填充识别记录
     *
     * @param ocrRecord
     */
    private void fillOcrRecord(OcrRecord ocrRecord) {
        ocrRecord.setId(UUID.randomUUID().toString());

        ocrRecord.setOperationDate(DateTimeUtil.getNowDate());
        ocrRecord.setCreator(ocrRecord.getOperatorId());
        ocrRecord.setCreatorName(ocrRecord.getOperatorName());
    }


    private void buildQuery(OcrRecord ocrRecord) {
        if (null != ocrRecord) {
            Date operationStartDate = ocrRecord.getOperationStartDate();
            if (null != operationStartDate)
                ocrRecord.setOperationStartDate(DateTimeUtil.getFirstSecondOfDay(operationStartDate));
            Date operationEndDate = ocrRecord.getOperationEndDate();
            if (null != operationEndDate)
                ocrRecord.setOperationEndDate(DateTimeUtil.getLastSecondOfDay(operationEndDate));
        }
    }

}                                                                                                        
