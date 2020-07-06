package com.thc.platform.modules.wechat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 租户公众号、小程序绑定的开发平台的openAppId
 * <p>
 * 目前一个租户只允许有一个openAppId，其下的所有公众号和小程序都必须绑定在该openAppId下
 *
 * @author zw
 */
@Data
@Accessors(chain = true)
@TableName("we_chat_open")
public class WeChatOpenEntity {

    @TableId(type = IdType.INPUT)
    private String id;
    /**
     * 租户ID
     */
    private Integer tenantId;
    /**
     * 开放平台帐号的appId
     */
    private String openAppId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    protected Date modifyTime;
}

