package com.thc.platform.modules.wechat.constant;


/**
 * @author ZM.Wang
 */
public interface WeChatConstant {

    /**
     * 微信相关接口访问前缀
     */
    String PATH_PREFIX = "/weChat";

    /**
     * 模板消息中小程序链接固定参数
     */
    String MINI_URL_FIXED_PARAM = "openChannel=message";

    /**
     * 公众号代开发管理员授权地址模板
     */
    String AUTH_URL = "https://mp.weixin.qq.com/safe/bindcomponent?action=bindcomponent&auth_type={0}&no_scan=1&component_appid={1}&pre_auth_code={2}&redirect_uri={3}&biz_appid={4}#wechat_redirect";

    /**
     * 互联网接收公众号事件消息路径
     */
    String PUBLIC_EVENT_URL_PATH = "/api-ih/titan-member/wx/receivePublicEventMsg";

    /**
     * 微信相关redis存储的key前缀
     */
    String WE_CHAT_REDIS_PREFIX = "GLOBAL-EXTEND:WE-CHAT:";

    /**
     * component_verify_ticket在redis中的key
     * 微信服务器 每隔10分钟会向第三方的消息接收地址推送一次component_verify_ticket，用于获取第三方平台接口调用凭据
     */
    String WE_CHAT_COMPONENT_VERIFY_TICKET_KEY = WE_CHAT_REDIS_PREFIX + "COMPONENT:VERIFY_TICKET";
    /**
     * 第三方平台access_token在redis中的key
     */
    String WE_CHAT_COMPONENT_ACCESS_TOKEN_KEY = WE_CHAT_REDIS_PREFIX + "COMPONENT:ACCESS_TOKEN";


    /**
     * 公众号 authorizer_access_token(授权第三方) 在redis中的key的前缀
     */
    String AUTHORIZER_ACCESS_TOKEN_REDIS_KEY = WE_CHAT_REDIS_PREFIX + "AUTHORIZER_ACCESS_TOKEN:";
    /**
     * 公众号 authorizer_refresh_token(授权第三方) 在redis中的key的前缀
     */
    String AUTHORIZER_REFRESH_TOKEN_REDIS_KEY = WE_CHAT_REDIS_PREFIX + "AUTHORIZER_REFRESH_TOKEN:";
    /**
     * appId authorizer_ticket(授权第三方) 在redis中的key的前缀
     */
    String AUTHORIZER_TICKET_REDIS_KEY = WE_CHAT_REDIS_PREFIX + "AUTHORIZER_TICKET:";


    /**
     * 小程序 access_token 在redis中的key的前缀,后面需拼接对应小程序的appId
     */
    String MINI_ACCESS_TOKEN_KEY_PREFIX = WE_CHAT_REDIS_PREFIX + "MINI_ACCESS_TOKEN:";

    /**
     * 发送模板消息总开关 OK=发送|其他为不发送
     */
    String TEMPLATE_MSG_SEND_FLAG = WE_CHAT_REDIS_PREFIX + "TEMPLATE_MSG_SEND_FLAG";

    /**
     * 根据appId获取租户的域名 redis key prefix
     */
    String TENANT_HOST_BY_APPID_PREFIX = WE_CHAT_REDIS_PREFIX + "HOST-BY-APPID:";

    /**
     * 根据appId获取租户的域名 redis key expires hours
     */
    int TENANT_HOST_BY_APPID_EXPIRE_HOURS = 2;

    /**
     * 可以发送模板消息的标志值
     */
    String TEMPLATE_MSG_CAN_SEND = "OK";
}
