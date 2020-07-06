package com.thc.platform.modules.ocr.util.task;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.dto.BillListAndBillDetailListDto;
import com.thc.platform.external.dto.ServicePriceIn;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.job.entity.JobLogEntity;
import com.thc.platform.modules.job.service.JobLogService;
import com.thc.platform.modules.ocr.bean.OcrInvokeStatistical;
import com.thc.platform.modules.ocr.biz.IOcrInvokeStatisticalService;
import com.thc.platform.modules.ocr.biz.IOcrRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@Slf4j
public class OcrInvokerTask {


    private static final String JOB_KEY = "GLOBAL-EXTEND:OCR:OCR-INVOKER-JOB:";
    private static final String JOB_NAME = "OCR-INVOKER-STATISTICAL-JOB";

    //rabbitMQ常量
    private static final String EXCHANGE_NAME = "exchange.global-platform.account";
    private static final String ROUTING_KEY_BILL = "bill";
    private static final String ROUTING_KEY_DETAIL = "billDetail";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private GlobalPlatformService globalPlatformService;
    @Resource
    private JobLogService jobLogService;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private IOcrInvokeStatisticalService ocrInvokeStatisticalService;
    @Resource
    private IOcrRecordService ocrRecordService;

    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void task() {
        String stDay = getStDay();
        log.info("***** 定时任务: 日统计OCR识别成功次数 start, 结算日期: {} *****", stDay);
        JobLogEntity jobLog = new JobLogEntity();
        jobLog.setJobName(JOB_NAME);
        String jobKey = this.getLockKey(stDay);
        String lock = ShortUUID.uuid();
        jobLog.setId(lock);
        redisTemplate.opsForValue().setIfAbsent(jobKey, lock);
        String cacheLock = redisTemplate.opsForValue().get(jobKey);
        if (lock.equals(cacheLock)) {
            redisTemplate.expire(jobKey, 5, TimeUnit.HOURS);
            try {
                Date startTime = new Date();
                jobLog.setStartTime(startTime);
                statisticsAndSaveResult(stDay);
                Date endTime = new Date();
                jobLog.setEndTime(endTime);
                jobLog.setConsumeMillis(endTime.getTime() - startTime.getTime());
                jobLog.setStatus(JobLogEntity.STATUS_SUCCESS);
            } catch (Exception e) {
                log.error("", e);
                jobLog.setNotes(e.getMessage());
                jobLog.setStatus(JobLogEntity.STATUS_FAIL);
                redisTemplate.delete(jobKey);
            }
        } else {
            jobLog.setStatus(JobLogEntity.STATUS_LOCK_FAIL);
            jobLog.setNotes("locked job id: " + cacheLock);
        }
        jobLog.setCreateTime(new Date());
        jobLogService.save(jobLog);
        log.info("***** 定时任务: 日统计OCR识别成功次数 end, 结算日期: {} *****", stDay);
    }

    public void statisticsAndSaveResult(String stDay) {

        List<BillListAndBillDetailListDto.TenantAccountBillDetail> tenantAccountBillDetailList = new ArrayList<>();
        //获取租户列表
        List<OcrInvokeStatistical> ocrInvokeStatisticalList = ocrRecordService.getCountByTenantIdAndOrgId(stDay);
        // 保存到统计表
        ocrInvokeStatisticalService.batchInsert(ocrInvokeStatisticalList);
        Map<String, List<OcrInvokeStatistical>> tenantList = ocrInvokeStatisticalList.stream().collect(Collectors.groupingBy(OcrInvokeStatistical::getTenantId));
        tenantList.forEach((tenantId, result) -> {
            ServicePriceIn in = new ServicePriceIn();
            in.setTenantId(tenantId);
            //设置服务类型
            in.setType(ServicePriceIn.TYPE_OCR);
            //获取服务单价
            BigDecimal price = globalPlatformService.getPriceByTenantIdAndType(in);
            BigDecimal totalConsumeMoney = new BigDecimal(0);

            for (OcrInvokeStatistical ocrInvokeStatistical : result) {
                BillListAndBillDetailListDto.TenantAccountBillDetail detail = new BillListAndBillDetailListDto.TenantAccountBillDetail();
                detail.setOrderId(getOrderId(stDay, tenantId, ocrInvokeStatistical.getOrgId()));
                detail.setTenantId(tenantId);
                detail.setOrgId(ocrInvokeStatistical.getOrgId());
                detail.setOrgName(ocrInvokeStatistical.getOrgName());
                //设置服务类型
                detail.setServiceType(BillListAndBillDetailListDto.SERVICE_TYPE_OCR);
                detail.setBillDate(stDay);
                detail.setServiceCount(ocrInvokeStatistical.getNumber());
                //计算消费金额，单价*数量
                BigDecimal consumeMoney = price.multiply(new BigDecimal(ocrInvokeStatistical.getNumber()));
                detail.setConsumeMoney(consumeMoney);

                tenantAccountBillDetailList.add(detail);

                //计算当前租户总消费金额
                totalConsumeMoney = totalConsumeMoney.add(consumeMoney);
            }
            BillListAndBillDetailListDto.TenantAccountBill bill = new BillListAndBillDetailListDto.TenantAccountBill();
            bill.setTenantId(tenantId);
            //设置费用类型
            bill.setFeeType(BillListAndBillDetailListDto.FEE_TYPE_DEDUCTION_OCR);
            bill.setMoney(totalConsumeMoney);
            bill.setOrderId(getOrderId(stDay, tenantId, null));
            //发送账单明细
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_DETAIL, JSON.toJSONString(tenantAccountBillDetailList));
            //发送账单
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_BILL, JSON.toJSONString(bill));

        });
    }

    // 获取统计天 yyyy-MM-dd 格式
    private String getStDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(calendar.getTime());
    }

    private String getOrderId(String stDay, String tenantId, String orgId) {
        StringBuilder sb = new StringBuilder();
        for (String s : stDay.split("-"))
            sb.append(s);

        sb.append(BillListAndBillDetailListDto.FEE_TYPE_DEDUCTION_OCR).append(tenantId);
        if (orgId != null) {
            sb.append(orgId);
        }
        return sb.toString();
    }


    private String getLockKey(String stDay) {
        return JOB_KEY + stDay;
    }
}
