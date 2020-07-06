package com.thc.platform.modules.express.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExpressResp {
    //消息体，请忽略
    private String message;
    //快递单当前状态，包括0在途，1揽收，2疑难，3签收，4退签，5派件，6退回，7转投 等8个状态
    private String state;
    //通讯状态，请忽略
    private String status;
    //快递单明细状态标记，暂未实现，请忽略
    private String condition;
    //是否签收标记
    private String ischeck;
    //快递公司编码,一律用小写字母，点击查看快递公司编码
    private String com;
    //快递单号
    private String nu;
    //数组，包含多个对象，每个对象字段如展开所示
    private List<DataEntity> data;

    @Data
    private static
    class DataEntity{
        //物流轨迹节点内容
        private String context;
        //时间，原始格式
        private String time;
        //格式化后时间
        private String ftime;
        //本数据元对应的签收状态。只有在开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
        private String status;
        //本数据元对应的行政区域的编码，只有在开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
        private String areaCode;
        //本数据元对应的行政区域的名称，开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
        private String areaName;
    }
}
