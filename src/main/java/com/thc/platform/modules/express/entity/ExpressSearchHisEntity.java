package com.thc.platform.modules.express.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
*  express_search_his
* @author Yapu 2019-11-12
*/
@Data
@TableName("express_search_his")
public class ExpressSearchHisEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    @TableId(type = IdType.INPUT)
    private String id;

    /**
    * 租户id
    */
    private String tenantId;

    /**
     * 机构id
     */
    private String orgId;
    /**
     * 机构id
     */
    private String orgName;

    /**
    * 请求消息
    */
    private String req;

    /**
    * 返回消息
    */
    private String resp;

    /**
    * 查询的快递公司的编码，一律用小写字母
    */
    private String com;

    /**
    * 查询的快递单号， 单号的最大长度是32个字符
    */
    private String num;
    /**
     * 查询人
     */
    private String creator;

    private Date createTime;

    public ExpressSearchHisEntity() {
    }

}