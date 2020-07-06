package com.thc.platform.modules.wechat.handler.publicmsg;

import com.alibaba.fastjson.JSON;
import com.thc.platform.modules.wechat.service.AccessTokenService;
import com.thc.platform.modules.wechat.service.WeChatPublicService;
import com.titan.wechat.common.api.basic.WxBasicApi;
import com.titan.wechat.common.api.basic.WxUserInfo;
import com.titan.wechat.common.api.business.dto.publicmsg.PublicEventMsgRequest;
import com.titan.wechat.common.api.message.WxMsgHandler;
import com.titan.wechat.common.api.message.WxReply;
import com.titan.wechat.common.api.message.received.msg.WxSubscribeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author ZW
 * 普通关注事件
 */
@Slf4j
@Component
public class WxSubscribeEventHandler implements WxMsgHandler<WxSubscribeEvent> {

    private final WxBasicApi wxBasicApi;
    private final AccessTokenService accessTokenService;
    private final WeChatPublicService weChatPublicService;

    public WxSubscribeEventHandler(WxBasicApi wxBasicApi, AccessTokenService accessTokenService, WeChatPublicService weChatPublicService) {
        this.wxBasicApi = wxBasicApi;
        this.accessTokenService = accessTokenService;
        this.weChatPublicService = weChatPublicService;
    }

    @Override
    public WxReply process(WxSubscribeEvent event) {
        String openId = event.fromUserName;
        log.info("普通关注事件，openId:{}, msgType:{}", openId, event.msgType);
        //查询用户信息 -获取unionId
        String accessToken = accessTokenService.getAuthorizerAccessToken(event.appId);
        WxUserInfo userInfo = wxBasicApi.getUserInfo(openId, accessToken);
        if (userInfo.successful()) {
            String unionId = userInfo.getUnionId();
            if (StringUtils.isNotEmpty(unionId)) {
                //调用互联网医院接口更新用户信息
                PublicEventMsgRequest request = PublicEventMsgRequest.buildBaseRequest(event, PublicEventMsgRequest.EventType.SUBSCRIBE);
                request.setData(JSON.toJSONString(event));
                request.setExtra(JSON.toJSONString(userInfo));
                request.setUnionId(unionId);
                weChatPublicService.sendEventMsgToTitan(request);
            } else {
                log.error("普通关注事件，openId:{}, unionId为空", openId);
            }
        }
        return WxReply.SUCCESS_REPLY;
    }

    @Override
    public Class<WxSubscribeEvent> getMsgClass() {
        return WxSubscribeEvent.class;
    }
}
