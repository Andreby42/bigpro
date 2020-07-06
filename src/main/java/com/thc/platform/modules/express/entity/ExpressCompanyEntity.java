package com.thc.platform.modules.express.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
*  express_company
* @author Yapu 2019-11-14
*/
@Data
@TableName("express_company")
public class ExpressCompanyEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * id
    */
    private Integer id;

    /**
    * 快递公司中文名称
    */
    private String com;

    /**
    * 快递公司编码
    */
    private String comCode;


}