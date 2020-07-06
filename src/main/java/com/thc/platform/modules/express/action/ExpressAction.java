package com.thc.platform.modules.express.action;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.modules.express.dto.ExpressResp;
import com.thc.platform.modules.express.dto.ExpressSearchIn;
import com.thc.platform.modules.express.job.ExpressSearchByOrgDayJob;
import com.thc.platform.modules.express.service.ExpressCompanyService;
import com.thc.platform.modules.express.service.ExpressSearchHisService;
import com.thc.platform.modules.express.util.KDMappingJackson2HttpMessageConverter;
import com.thc.platform.modules.express.util.MD5Utils;
import com.thc.platform.modules.sms.job.StatisticsTenantOrgDaySendJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * express_search_his
 *
 * @author Yapu 2019-11-12
 */
@RestController
@RequestMapping("/express")
public class ExpressAction {

    private static final Logger logger = LoggerFactory.getLogger(ExpressAction.class);
    private static final String AUTO_URL = "http://www.kuaidi100.com/autonumber/auto";
    private static final String QUERY_URL = "http://poll.kuaidi100.com/poll/query.do";

    @Resource
    private ExpressSearchHisService service;
    @Resource
    private ExpressCompanyService companyService;

    @Resource
    private ExpressSearchByOrgDayJob job;
    @Resource
    private StatisticsTenantOrgDaySendJob smsJob;

    @Value("${kuaidi100.key}")
    private String key;
    @Value("${kuaidi100.customer}")
    private String customer;

    /**
     * 智能判断所属快递公司
     */
    @PostMapping("/autoNumber")
    public Object autoNumber(@RequestBody ExpressSearchIn in) {
        in.validate();
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("key", key);
        postParameters.add("num", in.getNum());
        RestTemplate restTemplate = new RestTemplate();
        //添加支持多种MediaType格式
        restTemplate.getMessageConverters().add(new KDMappingJackson2HttpMessageConverter());
        try {
            return restTemplate.postForObject(AUTO_URL, postParameters, Object.class);
        } catch (Exception e) {
            logger.warn("数据出错：{}", restTemplate.postForObject(AUTO_URL, postParameters, Object.class));
            logger.error("错误信息：{}", e.getMessage());
            throw BEUtil.failCauseConfigData("查询出错，请稍后再试");
        }
    }

    /**
     * 查询快递单号
     */
    @PostMapping("/query")
    public Object query(@RequestBody ExpressSearchIn in) {
        //检查余额
        if (service.getTenantAccountBalance(in).compareTo(BigDecimal.ZERO) < 1) {
            throw BEUtil.failBusiRuleLimit("账户余额不足，请及时充值！");
        }
        in.checkQuery();
        String param = JSON.toJSON(in).toString();
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("customer", customer);
        postParameters.add("sign", MD5Utils.encode(param + key + customer));
        postParameters.add("param", param);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<MultiValueMap<String, Object>> r = new HttpEntity<>(postParameters, headers);
        RestTemplate restTemplate = new RestTemplate();
        //添加支持多种MediaType格式
        restTemplate.getMessageConverters().add(new KDMappingJackson2HttpMessageConverter());
        try {
            ExpressResp resp = restTemplate.postForObject(QUERY_URL, r, ExpressResp.class);
            service.addQueryRecord(in, postParameters, resp);
            return Api.ok(resp);
        } catch (Exception e) {
            logger.warn("数据出错：{}", restTemplate.postForObject(QUERY_URL, r, Object.class));
            logger.error("错误信息：{}", e.getMessage());
            throw BEUtil.failCauseConfigData("查询出错，请稍后再试");
        }
    }

    /**
     * 根据物流公司名称/编码查询物流公司
     */
    @PostMapping("/queryCom")
    public Object queryCom(@RequestBody ExpressSearchIn in) {
//        in.checkQueryCom();
        return Api.ok(companyService.queryCom(in));
    }

    /**
     * 根据id查询物流公司
     */
    @PostMapping("/queryComById")
    public Object queryComById(@RequestBody Map<String,String> map) {
        if(map.get("id")==null){
            throw BEUtil.failBusiRuleLimit("id不能为空");
        }
        return Api.ok(companyService.getById(map.get("id")));
    }

    //测试job
    @PostMapping("/test/job")
    public void testJob(){
       job.doJob();
       smsJob.doJob();
    }
}
