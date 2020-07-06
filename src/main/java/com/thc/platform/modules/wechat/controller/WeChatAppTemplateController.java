package com.thc.platform.modules.wechat.controller;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.modules.wechat.constant.WeChatConstant;
import com.thc.platform.modules.wechat.dto.TemplateBaseTypeOut;
import com.thc.platform.modules.wechat.dto.WeChatAppTemplateOut;
import com.thc.platform.modules.wechat.dto.WeChatTemplateCreateIn;
import com.thc.platform.modules.wechat.entity.WeChatAppTemplateEntity;
import com.thc.platform.modules.wechat.service.WeChatAppTemplateService;
import com.titan.wechat.common.api.business.enums.TemplateBaseTypeEnum;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description
 * @Author ZWen
 * @Date 2019/12/2 3:16 PM
 * @Version 1.0
 **/
@io.swagger.annotations.Api(tags = "微信-公众号模板")
@RestController
@RequestMapping(WeChatConstant.PATH_PREFIX + "/template")
public class WeChatAppTemplateController {

    private final WeChatAppTemplateService weChatAppTemplateService;

    public WeChatAppTemplateController(WeChatAppTemplateService weChatAppTemplateService) {
        this.weChatAppTemplateService = weChatAppTemplateService;
    }

    @ApiOperation("根据公众号appId查询模板列表(不分页)")
    @GetMapping("/listByAppId")
    public Api<List<WeChatAppTemplateOut>> listByAppId(@RequestParam String appId) {
        return Api.ok(weChatAppTemplateService.listByAppId(appId));
    }

    @ApiOperation("获取基础模板列表(不分页)")
    @GetMapping("/baseTemplateList")
    public Api<List<TemplateBaseTypeOut>> baseTemplateList() {
        return Api.ok(Arrays.stream(TemplateBaseTypeEnum.values()).map(TemplateBaseTypeOut::new).collect(Collectors.toList()));
    }

    @ApiOperation("创建公众号模板")
    @PostMapping("/save")
    public Api<WeChatAppTemplateEntity> saveTemplate(@RequestBody WeChatTemplateCreateIn weChatTemplateCreateIn) {
        weChatTemplateCreateIn.validate();
        WeChatAppTemplateEntity entity = weChatAppTemplateService.saveTemplate(weChatTemplateCreateIn);
        return Api.ok(entity);
    }

    @ApiOperation("删除模板")
    @GetMapping("/delete")
    public Api delete(@RequestParam String id) {
        return Api.ok(weChatAppTemplateService.delTemplate(id));
    }
}