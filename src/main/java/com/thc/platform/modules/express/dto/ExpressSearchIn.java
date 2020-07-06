package com.thc.platform.modules.express.dto;

import com.thc.platform.common.util.BEUtil;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
public class ExpressSearchIn {
    //我方分配给贵司的的公司编号, 点击查看账号信息
    private String customer;
    //签名， 用于验证身份， 按param + key + customer 的顺序进行MD5加密（注意加密后字符串要转大写）， 不需要“+”号
    private String sign;
    //查询的快递公司的编码，一律用小写字母
    private String com;
    //查询的快递单号， 单号的最大长度是32个字符
    private String num;
    //添加此字段表示开通行政区域解析功能。0：关闭（默认），1：开通行政区域解析功能，2：开通行政解析功能并且返回出发、目的及当前城市信息
    private int resultv2;
    //收件人或寄件人的手机号或固话（顺丰单号必填，也可以填写后四位，如果是固话，请不要上传分机号）
    private String phone;
    //出发地城市，省-市-区
    private String from;
    //目的地城市，省-市-区
    private String to;
    //返回结果排序方式。desc：降序（默认），asc：升序
    private String order;

    //token参数
    private String tenantName;
    private String tenantId;
    private String userId;
    private String organizationId;
    private String organizationName;
    private String displayName;

    //查询快递公司参数名称
    private String name;
    private int offset = 0;
    private int pagesize = 100;


    public void validate() {
        if (StringUtils.isEmpty(num)) {
            throw BEUtil.illegalFormat("快递单号不能为空！");
        }
    }

    public void checkQuery() {
        if (StringUtils.isEmpty(com)) {
            throw BEUtil.illegalFormat("公司编码不能为空！");
        }
    }

    public void checkQueryCom() {
        if (StringUtils.isEmpty(name)) {
            throw BEUtil.illegalFormat("搜索条件不能为空！");
        }
    }
}
