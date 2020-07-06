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
import com.titan.wechat.common.api.message.received.msg.WxScanEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author ZWen
 * 扫描带参数二维码事件：2.用户已关注时的事件推送
 */
@Slf4j
@Component
public class WxScanEventHandler implements WxMsgHandler<WxScanEvent> {

    private final AccessTokenService accessTokenService;
    private final WxBasicApi wxBasicApi;
    private final WeChatPublicService weChatPublicService;
    private final ScanSceneHandler scanSceneHandler;

    public WxScanEventHandler(AccessTokenService accessTokenService, WxBasicApi wxBasicApi, WeChatPublicService weChatPublicService, ScanSceneHandler scanSceneHandler) {
        this.accessTokenService = accessTokenService;
        this.wxBasicApi = wxBasicApi;
        this.weChatPublicService = weChatPublicService;
        this.scanSceneHandler = scanSceneHandler;
    }

    @Override
    public WxReply process(WxScanEvent event) {
        log.info("已关注带参数扫码，openId:{}, eventKey:{}, ticket:{}", event.fromUserName, event.eventKey, event.ticket);
        //查询用户信息 -获取unionId
        String accessToken = accessTokenService.getAuthorizerAccessToken(event.appId);
        WxUserInfo userInfo = wxBasicApi.getUserInfo(event.fromUserName, accessToken);
        if (userInfo.successful()) {
            scanSceneHandler.process(event.eventKey,userInfo);
            String unionId = userInfo.getUnionId();
            if (StringUtils.isNotEmpty(unionId)) {
                //调用互联网医院接口更新用户信息
                PublicEventMsgRequest request = PublicEventMsgRequest.buildBaseRequest(event, PublicEventMsgRequest.EventType.SCAN);
                request.setData(JSON.toJSONString(event));
                request.setExtra(JSON.toJSONString(userInfo));
                request.setUnionId(unionId);
                weChatPublicService.sendEventMsgToTitan(request);
            } else {
                log.error("已关注带参数扫码，openId:{}, unionId为空", event.fromUserName);
            }
        }
        return WxReply.SUCCESS_REPLY;
    }

    @Override
    public Class<WxScanEvent> getMsgClass() {
        return WxScanEvent.class;
    }
}
