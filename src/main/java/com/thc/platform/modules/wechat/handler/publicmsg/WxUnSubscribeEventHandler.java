package com.thc.platform.modules.wechat.handler.publicmsg;

import com.alibaba.fastjson.JSON;
import com.thc.platform.modules.wechat.service.WeChatPublicService;
import com.titan.wechat.common.api.business.dto.publicmsg.PublicEventMsgRequest;
import com.titan.wechat.common.api.message.WxMsgHandler;
import com.titan.wechat.common.api.message.WxReply;
import com.titan.wechat.common.api.message.received.msg.WxUnsubscribeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


/**
 * @author ZM.Wang
 * 取消关注处理类
 */
@Slf4j
@Component
public class WxUnSubscribeEventHandler implements WxMsgHandler<WxUnsubscribeEvent> {

    private final WeChatPublicService weChatPublicService;

    public WxUnSubscribeEventHandler(WeChatPublicService weChatPublicService) {
        this.weChatPublicService = weChatPublicService;
    }

    @Override
    public WxReply process(WxUnsubscribeEvent wxUnsubscribeEvent) {
        String openId = wxUnsubscribeEvent.fromUserName;
        log.info("[" + openId + "]取消关注");
        //调用互联网医院接口更新用户信息
        PublicEventMsgRequest request = PublicEventMsgRequest.buildBaseRequest(wxUnsubscribeEvent, PublicEventMsgRequest.EventType.UN_SUBSCRIBE);
        request.setData(JSON.toJSONString(wxUnsubscribeEvent));
        weChatPublicService.sendEventMsgToTitan(request);
        return WxReply.SUCCESS_REPLY;
    }

    @Override
    public Class<WxUnsubscribeEvent> getMsgClass() {
        return WxUnsubscribeEvent.class;
    }
}
