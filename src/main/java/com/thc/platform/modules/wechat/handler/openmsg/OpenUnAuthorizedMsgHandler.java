package com.thc.platform.modules.wechat.handler.openmsg;

import com.thc.platform.modules.wechat.service.WeChatAppInfoService;
import com.titan.wechat.common.api.open.OpenMsgHandler;
import com.titan.wechat.common.api.open.OpenReply;
import com.titan.wechat.common.api.open.event.received.OpenUnAuthorizedMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @Description 取消授权处理
 * @Author ZWen
 * @Date 2018/12/30 8:46 PM
 * @Version 1.0
 **/
@Component
public class OpenUnAuthorizedMsgHandler implements OpenMsgHandler<OpenUnAuthorizedMsg> {

    private static final Logger logger = LoggerFactory.getLogger(OpenUnAuthorizedMsgHandler.class);

    private final WeChatAppInfoService weChatAppInfoService;

    public OpenUnAuthorizedMsgHandler(WeChatAppInfoService weChatAppInfoService) {
        this.weChatAppInfoService = weChatAppInfoService;
    }

    @Override
    public OpenReply process(OpenUnAuthorizedMsg msg) {
        logger.error("取消授权通知, {}", msg);
        //取消授权更新数据库
        weChatAppInfoService.unAuth(msg.getAuthorizerAppId());
        return OpenReply.SUCCESS_REPLY;
    }

    @Override
    public Class<OpenUnAuthorizedMsg> getMsgClass() {
        return OpenUnAuthorizedMsg.class;
    }
}