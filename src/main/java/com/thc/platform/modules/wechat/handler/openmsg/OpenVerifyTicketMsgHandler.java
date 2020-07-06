package com.thc.platform.modules.wechat.handler.openmsg;

import com.thc.platform.modules.wechat.service.AccessTokenService;
import com.thc.platform.modules.wechat.service.WxRedisService;
import com.titan.wechat.common.api.open.OpenMsgHandler;
import com.titan.wechat.common.api.open.OpenReply;
import com.titan.wechat.common.api.open.event.received.OpenVerifyTicketMsg;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @Description 验证票据（component_verify_ticket），在第三方平台创建审核通过后，微信服务器会向其 ”授权事件接收URL” 每隔 10 分钟以 POST 的方式推送 component_verify_ticket
 * @Author ZWen
 * @Date 2018/12/30 6:33 PM
 * @Version 1.0
 **/
@Component
public class OpenVerifyTicketMsgHandler implements OpenMsgHandler<OpenVerifyTicketMsg> {

    private final WxRedisService wxRedisService;
    private final AccessTokenService accessTokenService;

    public OpenVerifyTicketMsgHandler(WxRedisService wxRedisService, AccessTokenService accessTokenService) {
        this.wxRedisService = wxRedisService;
        this.accessTokenService = accessTokenService;
    }

    @Override
    public OpenReply process(OpenVerifyTicketMsg msg) {
        if (StringUtils.isNotEmpty(msg.getVerifyTicket())) {
            //设置两个小时的过期时间
            wxRedisService.setAndExpireOpenVerifyTicket(msg.getVerifyTicket());
        }
        //判断OpenAccessToken是否过期，如果过期，刷新
        String openAccessToken = wxRedisService.getOpenAccessToken();
        if (StringUtils.isEmpty(openAccessToken)) {
            accessTokenService.refreshOpenAccessToken(msg.getVerifyTicket());
        }
        return OpenReply.SUCCESS_REPLY;
    }

    @Override
    public Class<OpenVerifyTicketMsg> getMsgClass() {
        return OpenVerifyTicketMsg.class;
    }
}