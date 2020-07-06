package com.thc.platform.modules.express.job;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.external.dto.BillListAndBillDetailListDto;
import com.thc.platform.external.dto.ServicePriceIn;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.express.dto.OrgDaySearch;
import com.thc.platform.modules.express.service.ExpressSearchHisService;
import com.thc.platform.modules.job.entity.JobLogEntity;
import com.thc.platform.modules.job.service.JobLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ExpressSearchByOrgDayJob {
	
	private static final Logger logger = LoggerFactory.getLogger(ExpressSearchByOrgDayJob.class);
	
	private static final String JOB_KEY = "GLOBAL-EXTEND:EXPRESS:pushExpressSearchByOrgDayJob:";
	private static final String JOB_NAME = "push-express-search-job";

	//rabbitMQ常量
	private static final String EXCHANGE_NAME = "exchange.global-platform.account";
	private static final String ROUTING_KEY_BILL = "bill";
	private static final String ROUTING_KEY_DETAIL = "billDetail";

	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Resource
	private JobLogService jobLogService;
	@Resource
	private ExpressSearchHisService expressSearchHisService;
	@Resource
	private GlobalPlatformService globalPlatformService;
	@Resource
	private RabbitTemplate rabbitTemplate;

	@Scheduled(cron="0 0 2 * * ? ")
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
	
	// 获取统计天 yyyy-MM-dd 格式
	private String getStDay() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(calendar.getTime());
	}

	public void statisticsAndSaveResult(String stDay) {

		List<BillListAndBillDetailListDto.TenantAccountBillDetail> tenantAccountBillDetailList = new ArrayList<>();
		//获取租户列表
		List<String> tenantIdList = expressSearchHisService.getDisTenantIdByDay(stDay);

		for (String tenantId : tenantIdList) {
			//按机构分组获取使用服务数量，机构id、机构名称
			List<OrgDaySearch> orgDaySearchList = expressSearchHisService.getSearchCountByDay(tenantId, stDay);
			ServicePriceIn in = new ServicePriceIn();
			in.setTenantId(tenantId);
			//设置服务类型
			in.setType(ServicePriceIn.TYPE_EXPRESS);
			//获取服务单价
			BigDecimal price = globalPlatformService.getPriceByTenantIdAndType(in);

			BigDecimal totalConsumeMoney = new BigDecimal(0);
			//按机构生成消费账单明细
			for (OrgDaySearch orgDaySearch : orgDaySearchList) {
				BillListAndBillDetailListDto.TenantAccountBillDetail detail = new BillListAndBillDetailListDto.TenantAccountBillDetail();
				detail.setOrderId(getOrderId(stDay,tenantId,orgDaySearch.getOrgId()));
				detail.setTenantId(tenantId);
				detail.setOrgId(orgDaySearch.getOrgId());
				detail.setOrgName(orgDaySearch.getOrgName());
				//设置服务类型
				detail.setServiceType(BillListAndBillDetailListDto.SERVICE_TYPE_EXPRESS);
				detail.setBillDate(stDay);
				detail.setServiceCount(orgDaySearch.getDaySearchNum());
				//计算消费金额，单价*数量
				BigDecimal consumeMoney = price.multiply(new BigDecimal(orgDaySearch.getDaySearchNum()));
				detail.setConsumeMoney(consumeMoney);

				tenantAccountBillDetailList.add(detail);
				//计算当前租户总消费金额
				totalConsumeMoney = totalConsumeMoney.add(consumeMoney);
			}
			BillListAndBillDetailListDto.TenantAccountBill bill = new BillListAndBillDetailListDto.TenantAccountBill();
			bill.setTenantId(tenantId);
			//设置费用类型
			bill.setFeeType(BillListAndBillDetailListDto.FEE_TYPE_DEDUCTION_EXPRESS);
			bill.setMoney(totalConsumeMoney);
			bill.setOrderId(getOrderId(stDay,tenantId,null));
			//发送账单明细
			rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_DETAIL, JSON.toJSONString(tenantAccountBillDetailList));
			//发送账单
			rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY_BILL, JSON.toJSONString(bill));
		}
	}
	
	private String getOrderId(String stDay,String tenantId,String orgId) {
		StringBuilder sb = new StringBuilder();
		for(String s : stDay.split("-")) 
			sb.append(s);

		sb.append(BillListAndBillDetailListDto.FEE_TYPE_DEDUCTION_EXPRESS).append(tenantId);
		if(orgId!=null){
			sb.append(orgId);
		}
		return sb.toString();
	}

}
