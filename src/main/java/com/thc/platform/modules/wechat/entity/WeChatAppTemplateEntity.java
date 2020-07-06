package com.thc.platform.modules.wechat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.ModifyBaseEntity;
import com.titan.wechat.common.api.business.enums.TemplateBaseTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 微信公众号模板管理
 *
 * @author zhangwen
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("we_chat_app_template")
public class WeChatAppTemplateEntity extends ModifyBaseEntity {

    @TableId(type = IdType.INPUT)
    private String id;
    /**
     * 模板编码 {@link TemplateBaseTypeEnum}
     */
    private String code;
    /**
     * 模版标题  {@link TemplateBaseTypeEnum}
     */
    private String title;
    /**
     * 模板Id
     */
    private String templateId;
    /**
     * 模板所属的appId
     */
    private String appId;
    /**
     * app授权表id {@link WeChatAppInfoEntity}
     */
    private String appInfoId;
    /**
     * 租户Id
     */
    private Integer tenantId;
    /**
     * 模板状态 {@link com.thc.platform.modules.wechat.constant.WeChatEnum.AppTemplateStatus}
     */
    private Integer status;
}
