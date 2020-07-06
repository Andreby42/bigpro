package com.thc.platform.modules.wechat.controller;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.modules.wechat.constant.WeChatConstant;
import com.thc.platform.modules.wechat.dto.WeChatAppInfoOut;
import com.thc.platform.modules.wechat.service.WeChatAppInfoService;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/12/2 3:16 PM
 * @Version 1.0
 **/
@io.swagger.annotations.Api(tags = "微信-配置及授权")
@RestController
@RequestMapping(WeChatConstant.PATH_PREFIX + "/appInfo")
public class WeChatAppInfoController {

    private final WeChatAppInfoService weChatAppInfoService;

    public WeChatAppInfoController(WeChatAppInfoService weChatAppInfoService) {
        this.weChatAppInfoService = weChatAppInfoService;
    }

    @ApiOperation("获取租户的公众号或小程序配置列表")
    @GetMapping("/listByTenant")
    public Api<List<WeChatAppInfoOut>> listByTenant(@RequestParam String tenantId) {
        return Api.ok(weChatAppInfoService.listByTenant(tenantId));
    }

    /**
     * 后续废弃，使用{@link WeChatPublicController#getAuthUrl(String, Integer)}。前端需修改地址
     *
     * @param appId
     * @param typeCode
     * @return
     */
    @Deprecated
    @ApiOperation("获取公众号代开发管理员授权地址")
    @GetMapping("/getAuthUrl")
    public Api<String> getAuthUrl(@RequestParam String appId, @RequestParam Integer typeCode, @RequestParam Integer tenantId) {
        if(tenantId ==null){
            throw BEUtil.illegalFormat("租户ID不能为空");
        }
        weChatAppInfoService.checkIsAuth(appId,typeCode,tenantId);

        return Api.ok(weChatAppInfoService.getAuthUrl(appId, typeCode));
    }
}