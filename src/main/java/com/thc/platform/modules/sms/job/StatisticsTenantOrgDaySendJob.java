package com.thc.platform.modules.sms.job;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.dto.BillListAndBillDetailListDto;
import com.thc.platform.external.dto.ServicePriceIn;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.job.entity.JobLogEntity;
import com.thc.platform.modules.job.service.JobLogService;
import com.thc.platform.modules.sms.dto.TenantOrgDaySendDto;
import com.thc.platform.modules.sms.service.SendRecordService;

@Service
public class StatisticsTenantOrgDaySendJob {

	private static final Logger logger = LoggerFactory.getLogger(StatisticsTenantOrgDaySendJob.class);

	private static final String JOB_KEY = "GLOBAL-EXTEND:SMS:stOrgDaySendAndConsumeMoney:";
	private static final String JOB_NAME = "statistics-org-day-send-job";

	//rabbitMQ常量
	private static final String EXCHANGE_NAME = "exchange.global-platform.account";
	private static final String ROUTING_KEY_BILL = "bill";
	private static final String ROUTING_KEY_DETAIL = "billDetail";
		
	@Autowired
	private JobLogService jobLogService;
	@Autowired
	private SendRecordService sendRecordService;
	@Resource
	private GlobalPlatformService globalPlatformService;
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Resource
	private RabbitTemplate rabbitTemplate;
	
	@Scheduled(cron="0 0 1 * * ? ")
	public void doJob() {
		JobLogEntity jobLog = new JobLogEntity();
		jobLog.setJobName(JOB_NAME);

		String stDay = getStDay();
		String jobKey = JOB_KEY + stDay;

		String lock = ShortUUID.uuid();
		jobLog.setId(lock);

		redisTemplate.opsForValue().setIfAbsent(jobKey, lock);
		String cacheLock = redisTemplate.opsForValue().get(jobKey);
		logger.info("@@@stDay: " + stDay);
		if(lock.equals(cacheLock)) {
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
				logger.error("", e);
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
	}

	public void statisticsAndSaveResult(String stDay) {
		List<BillListAndBillDetailListDto.TenantAccountBillDetail> tenantAccountBillDetailList = new ArrayList<>();
		//获取租户列表
		List<Integer> tenantIds = sendRecordService.getDisTenantIdByDay(stDay);
		
		for (Integer tenantId : tenantIds) {
			//按机构分组获取使用服务数量，机构id、机构名称
			List<TenantOrgDaySendDto> tenantOrgDaySends = sendRecordService.stTenantOrgDaySend(tenantId, stDay);
			
			String tenantIdStr = tenantId.toString();
			
			ServicePriceIn in = new ServicePriceIn();
			in.setTenantId(tenantIdStr);
			//设置服务类型
			in.setType(ServicePriceIn.TYPE_SMS);
			//获取服务单价
			BigDecimal price = globalPlatformService.getPriceByTenantIdAndType(in);

			BigDecimal totalConsumeMoney = new BigDecimal(0);
			//按机构生成消费账单明细
			for (TenantOrgDaySendDto tenantOrgDaySend : tenantOrgDaySends) {
				BillListAndBillDetailListDto.TenantAccountBillDetail detail = new BillListAndBillDetailListDto.TenantAccountBillDetail();
				detail.setOrderId(getOrderId(stDay, tenantId, tenantOrgDaySend.getOrgId()));
				detail.setTenantId(tenantIdStr);
				detail.setOrgId(tenantOrgDaySend.getOrgId());
				detail.setOrgName(tenantOrgDaySend.getOrgName());
				//设置服务类型
				detail.setServiceType(BillListAndBillDetailListDto.SERVICE_TYPE_SMS);
				detail.setBillDate(stDay);
				detail.setServiceCount(tenantOrgDaySend.getFeeNum());
				//计算消费金额，单价*数量
				BigDecimal consumeMoney = price.multiply(new BigDecimal(tenantOrgDaySend.getFeeNum()));
				detail.setConsumeMoney(consumeMoney);

				tenantAccountBillDetailList.add(detail);
				//计算当前租户总消费金额
				totalConsumeMoney = totalConsumeMoney.add(consumeMoney);
			}
			BillListAndBillDetailListDto.TenantAccountBill bill = new BillListAndBillDetailListDto.TenantAccountBill();
			bill.setTenantId(tenantIdStr);
			//设置费用类型
			bill.setFeeType(BillListAndBillDetailListDto.FEE_TYPE_DEDUCTION_SMS);
			bill.setMoney(totalConsumeMoney);
			bill.setOrderId(getOrderId(stDay,tenantId,null));
			//发送账单明细
			rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_DETAIL, JSON.toJSONString(tenantAccountBillDetailList));
			//发送账单
			rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_BILL, JSON.toJSONString(bill));
		}
	}

	// 获取统计天 yyyy-MM-dd 格式
	private String getStDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(calendar.getTime());
	}

	private String getOrderId(String stDay, Integer tenantId,String orgId) {
		StringBuilder sb = new StringBuilder();
		for(String s : stDay.split("-")) 
			sb.append(s);

		sb.append(BillListAndBillDetailListDto.FEE_TYPE_DEDUCTION_SMS).append(tenantId);
		if(orgId!=null){
			sb.append(orgId);
		}
		return sb.toString();
	}
}
