package com.thc.platform.modules.wechat.handler.publicmsg;

import com.alibaba.fastjson.JSON;
import com.thc.platform.modules.wechat.handler.publicscan.ScanSceneHandler;
import com.thc.platform.modules.wechat.service.AccessTokenService;
import com.thc.platform.modules.wechat.service.WeChatPublicService;
import com.titan.wechat.common.api.basic.WxBasicApi;
import com.titan.wechat.common.api.basic.WxUserInfo;
import com.titan.wechat.common.api.business.dto.publicmsg.PublicEventMsgRequest;
import com.titan.wechat.common.api.message.WxMsgHandler;
import com.titan.wechat.common.api.message.WxReply;
import com.titan.wechat.common.api.message.received.msg.WxScanSubscribeEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author ZM.Wang
 * 带参数二维码-扫码关注事件
 */
@Slf4j
@Component
public class WxScanSubscribeEventHandler implements WxMsgHandler<WxScanSubscribeEvent> {

    private final WeChatPublicService weChatPublicService;
    private final AccessTokenService accessTokenService;
    private final WxBasicApi wxBasicApi;
    private final ScanSceneHandler scanSceneHandler;

    public WxScanSubscribeEventHandler(WeChatPublicService weChatPublicService, AccessTokenService accessTokenService, WxBasicApi wxBasicApi, ScanSceneHandler scanSceneHandler) {
        this.weChatPublicService = weChatPublicService;
        this.accessTokenService = accessTokenService;
        this.wxBasicApi = wxBasicApi;
        this.scanSceneHandler = scanSceneHandler;
    }

    @Override
    public WxReply process(WxScanSubscribeEvent event) {
        log.info("带参数二维码-扫码关注，openId:{}, eventKey:{}, ticket:{}", event.fromUserName, event.eventKey, event.ticket);
        //查询用户信息 -获取unionId
        String accessToken = accessTokenService.getAuthorizerAccessToken(event.appId);
        WxUserInfo userInfo = wxBasicApi.getUserInfo(event.fromUserName, accessToken);
        if (userInfo.successful()) {
            scanSceneHandler.process(event.eventKey,userInfo);
            String unionId = userInfo.getUnionId();
            if (StringUtils.isNotEmpty(unionId)) {
                //调用互联网医院接口更新用户信息
                PublicEventMsgRequest request = PublicEventMsgRequest.buildBaseRequest(event, PublicEventMsgRequest.EventType.SCAN_SUBSCRIBE);
                request.setData(JSON.toJSONString(event));
                request.setExtra(JSON.toJSONString(userInfo));
                request.setUnionId(unionId);
                weChatPublicService.sendEventMsgToTitan(request);
            } else {
                log.error("带参数二维码-扫码关注，openId:{}, unionId为空", event.fromUserName);
            }
        }
        return WxReply.SUCCESS_REPLY;
    }

    @Override
    public Class<WxScanSubscribeEvent> getMsgClass() {
        return WxScanSubscribeEvent.class;
    }
}
