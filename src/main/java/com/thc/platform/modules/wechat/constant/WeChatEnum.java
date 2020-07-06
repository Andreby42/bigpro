package com.thc.platform.modules.wechat.constant;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/12/2 11:59 AM
 * @Version 1.0
 **/
@SuppressWarnings("all")
public interface WeChatEnum {

    /**
     * 授权状态 0=待授权 1=已授权 2=已取消授权
     */
    enum AppAuthStatus {

        NO(0, "待授权"),
        YES(1, "已授权"),
        CANCEL(2, "已取消授权");

        final int code;
        final String msg;

        AppAuthStatus(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * APP类型  1=微信公众号 2=微信小程序
     */
    enum AppType {

        PUBLIC(1, "微信公众号"),
        MINI(2, "微信小程序");

        final int code;
        final String msg;

        AppType(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 模板状态 1=已启用 2=已删除(会调用接口删除对应微信公众号模板)
     */
    enum AppTemplateStatus {

        ALIVE(1, "正常启用"),
        DELETE(2, "已删除");

        final int code;
        final String msg;

        AppTemplateStatus(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }
    }
}