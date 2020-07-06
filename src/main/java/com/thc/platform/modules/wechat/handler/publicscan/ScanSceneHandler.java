package com.thc.platform.modules.wechat.handler.publicscan;

import com.thc.platform.modules.ocr.util.DateTimeUtil;
import com.thc.platform.modules.wechat.service.WeChatPublicService;
import com.titan.wechat.common.api.basic.WxUserInfo;
import com.titan.wechat.common.api.business.dto.TemplateMsgSendIn;
import com.titan.wechat.common.api.business.enums.ScanSceneTypeEnum;
import com.titan.wechat.common.api.message.send.template.TemplateMsgRequest;
import com.titan.wechat.common.api.message.send.template.data.TemplateMsgData;
import com.titan.wechat.common.api.message.send.template.data.TemplateMsgRowData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @Description 公众号永久二维码额外处理类
 * @Author ZWen
 * @Date 2020/3/2 2:16 PM
 * @Version 1.0
 **/
@Slf4j
@Component
public class ScanSceneHandler {

    @Value("${envInfo.envDomain}")
    private String envDomain;

    /**
     * todo:运营临时测试用，不再需要后删除
     */
    private static String TEST_SEND_REPORT_REDIS_KEY = "GLOBAL-EXTEND:WE-CHAT:SCAN-SCENE:TEST-SEND-REPORT";

    private final RedisTemplate<String, String> redisTemplate;
    private final WeChatPublicService weChatPublicService;

    public ScanSceneHandler(RedisTemplate<String, String> redisTemplate, WeChatPublicService weChatPublicService) {
        this.redisTemplate = redisTemplate;
        this.weChatPublicService = weChatPublicService;
    }

    /**
     * 处理
     *
     * @param eventKey 两种格式：已关注(eventKey:TEST-123456) 未关注(eventKey:qrscene_TEST-123456)
     * @param userInfo 用户信息
     */
    public void process(final String eventKey, WxUserInfo userInfo) {
        try {
            if (StringUtils.isEmpty(eventKey)) {
                log.error("ScanSceneHandler eventKey is empty");
                return;
            }
            if (eventKey.contains(ScanSceneTypeEnum.TEST_SEND_REPORT.name())) {
                testSendReportProcess(userInfo);
            }
        } catch (Exception e) {
            log.error("ScanSceneHandler error {}", e);
        }
    }

    /**
     * 发送统计日报类型扫码
     * 测试 将openId添加到redis
     *
     * @param userInfo
     */
    public void testSendReportProcess(WxUserInfo userInfo) {
        redisTemplate.opsForSet().add(TEST_SEND_REPORT_REDIS_KEY, userInfo.getOpenId());
    }

    /**
     * 每天早上8点执行一次，发送统计日报类型扫码
     */
    @Scheduled(cron = "0 0 8 * * ?")
//    @Scheduled(cron = "0 */1 * * * ?")
    public void testSendReportProcess() {
        if (envDomain.contains("dev")) {
            String now = DateTimeUtil.getDateTimeString(new Date());
            String key = "GLOBAL-EXTEND:WE-CHAT:SCAN-SCENE:TEST-SEND-REPORT-SCHEDULE";
            Boolean lock = redisTemplate.opsForValue().setIfAbsent(key, now, 1, TimeUnit.HOURS);
            if (lock != null && lock) {
                log.info("每天早上8点执行一次，测试发送统计日报");
                Set<String> members = redisTemplate.opsForSet().members(TEST_SEND_REPORT_REDIS_KEY);
                if (CollectionUtils.isNotEmpty(members)) {
                    TemplateMsgSendIn in = new TemplateMsgSendIn();
                    in.setAppId("wxecfca2237e1bcaf0");
                    in.setOpenIds(new ArrayList<>(members));
                    TemplateMsgRequest request = new TemplateMsgRequest();
                    in.setTemplateBody(request);
                    request.setUrl("http://testihpool.everjiankang.com.cn/web/titan-doctor/#/h5/wxdatereport?everJK=q7px4dv20bf");
                    request.setTemplateId("cPJdqPPBqwm0HeiMYyGEyq9jQ2BIz28RiCD_UPJGlz4");
                    TemplateMsgData msgData = new TemplateMsgData();
                    request.setData(msgData);
                    msgData.setFirst(new TemplateMsgRowData("您好，昨日日报已经生成，请查看"));
                    msgData.setKeyword1(new TemplateMsgRowData(DateTimeUtil.getDateString(new Date(), "yyyy年MM月dd日") + "测试机构日报"));
                    msgData.setKeyword2(new TemplateMsgRowData(now));
                    weChatPublicService.send(in);
                } else {
                    log.info("测试发送统计日报未执行，openId列表为空");
                }
            } else {
                log.info("测试发送统计日报未执行，已经由其他服务执行");
            }
        } else {
            log.info("测试发送统计日报未执行，非dev环境禁止执行");
        }

    }
}