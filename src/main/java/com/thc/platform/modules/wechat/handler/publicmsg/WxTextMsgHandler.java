package com.thc.platform.modules.wechat.handler.publicmsg;

import com.titan.wechat.common.api.message.WxMsgHandler;
import com.titan.wechat.common.api.message.WxReply;
import com.titan.wechat.common.api.message.received.msg.WxTextMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author ZW
 * 文本消息事件
 */
@Slf4j
@Component
public class WxTextMsgHandler implements WxMsgHandler<WxTextMsg> {

    @Override
    public WxReply process(WxTextMsg event) {
        log.debug("文本消息事件，openId:{}, 文本内容:{}", event.fromUserName, event.content);
        return WxReply.SUCCESS_REPLY;
    }

    @Override
    public Class<WxTextMsg> getMsgClass() {
        return WxTextMsg.class;
    }
}
