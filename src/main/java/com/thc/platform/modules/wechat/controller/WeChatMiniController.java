package com.thc.platform.modules.wechat.controller;

import com.thc.platform.modules.wechat.constant.WeChatConstant;
import com.thc.platform.modules.wechat.service.WeChatMiniService;
import com.titan.wechat.common.api.mini.req.GetMiniCodeIn;
import com.titan.wechat.common.api.mini.req.GetUnlimitMiniCodeIn;
import com.titan.wechat.common.api.support.WxStreamResponse;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @Description 小程序相关第三方接口
 * @Author ZWen
 * @Date 2018/12/30 2:59 PM
 * @Version 1.0
 **/
@io.swagger.annotations.Api(tags = "微信-小程序")
@Slf4j
@RestController
@RequestMapping(WeChatConstant.PATH_PREFIX + "/mini")
public class WeChatMiniController {

    private final WeChatMiniService wxMiniService;

    public WeChatMiniController(WeChatMiniService wxMiniService) {
        this.wxMiniService = wxMiniService;
    }

    @ApiOperation("生成小程序码(有限制)，返回图片流")
    @PostMapping("/getMiniLimitCode/{appId}")
    public void getMiniLimitCode(@PathVariable String appId, @RequestBody GetMiniCodeIn getMiniCodeIn, HttpServletResponse response) throws IOException {
        getMiniCodeIn.validate();
        WxStreamResponse streamResponse = wxMiniService.getMiniCodeImgLink(appId, getMiniCodeIn);
        if (streamResponse.getErrCode() != 0) {
            response.setStatus(555);
        }
        //响应流
        byte[] buf = new byte[1024];
        int length = 0;
        try (InputStream in = streamResponse.getInputStream(); OutputStream out = response.getOutputStream()) {
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
        }
    }

    @ApiOperation("生成小程序码(无限制)，返回图片流")
    @PostMapping("/getMiniUnlimitCode/{appId}")
    public void getMiniUnlimitCode(@PathVariable String appId, @RequestBody GetUnlimitMiniCodeIn getUnlimitMiniCodeIn, HttpServletResponse response) throws IOException {
        getUnlimitMiniCodeIn.validate();
        WxStreamResponse streamResponse = wxMiniService.getUnlimitMiniCodeImgLink(appId, getUnlimitMiniCodeIn);
        if (streamResponse.getErrCode() != 0) {
            log.error("getUnlimitMiniCodeImgLink error [{}],[{}]", streamResponse.getErrCode(), streamResponse.getErrMsg());
            response.setStatus(555);
        }
        //响应流
        byte[] buf = new byte[1024];
        int length = 0;
        try (InputStream in = streamResponse.getInputStream(); OutputStream out = response.getOutputStream()) {
            while ((length = in.read(buf)) != -1) {
                out.write(buf, 0, length);
            }
        }
    }
}