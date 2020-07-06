package com.thc.platform.modules.wechat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.modules.wechat.constant.WeChatEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 授权管理
 *
 * @author zw
 */
@Data
@Accessors(chain = true)
@TableName("we_chat_app_info")
public class WeChatAppInfoEntity {


    @TableId(type = IdType.INPUT)
    private String id;
    /**
     * 租户ID
     */
    private Integer tenantId;
    /**
     * app名称
     */
    private String appName;
    /**
     * app类型 {@link WeChatEnum.AppType}
     */
    private Integer appType;
    /**
     * appId
     */
    private String appId;
    /**
     * 绑定的开放平台帐号的appId
     */
    private String openAppId;
    /**
     * app授权状态 {@link WeChatEnum.AppAuthStatus}
     */
    private Integer status;
    /**
     * 授权给开发者的权限集列表，id集合，以逗号分隔
     * 参考链接：https://developers.weixin.qq.com/doc/oplatform/Third-party_Platforms/api/func_info.html
     */
    private String funcInfo;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    protected Date modifyTime;
}

