package com.thc.platform.modules.wechat.handler.publicmsg;

import com.titan.wechat.common.api.message.WxMsgHandler;
import com.titan.wechat.common.api.message.WxReply;
import com.titan.wechat.common.api.message.received.msg.WxUnTextMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author ZWen
 * 非文本消息事件
 */
@Slf4j
@Component
public class WxUnTextMsgHandler implements WxMsgHandler<WxUnTextMsg> {

    @Override
    public WxReply process(WxUnTextMsg event) {
        log.info("非文本消息事件，openId:{}, msgType:{}", event.fromUserName, event.msgType);
        return WxReply.SUCCESS_REPLY;
    }

    @Override
    public Class<WxUnTextMsg> getMsgClass() {
        return WxUnTextMsg.class;
    }
}
