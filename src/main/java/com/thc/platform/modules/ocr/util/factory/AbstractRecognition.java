package com.thc.platform.modules.ocr.util.factory;

import com.thc.platform.external.api.GlobalPlatformApi;
import com.thc.platform.modules.ocr.bean.OcrRecord;
import com.thc.platform.modules.ocr.util.ImgUtils;
import com.thc.platform.modules.ocr.util.SpringUtil;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
public abstract class AbstractRecognition<T> {

    protected abstract T doRecognition(OcrRecord ocrRecord);

    protected abstract OcrRecord transfer(OcrRecord ocrRecord, T result);

    public OcrRecord recognition(OcrRecord ocrRecord) {
        // 获取token
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("x-access-token");
        if (null != ocrRecord) {
            String imgUrl = ocrRecord.getUrl();
            if (StringUtils.isEmpty(imgUrl) || StringUtils.isEmpty(ocrRecord.getFileId())) {
                throw new RuntimeException("图片地址为空");
            }
            // 获取图片流，并保存到本地
            InputStream fileInputStream = this.getFileInputStream(ocrRecord.getFileId());
            // 处理图像，返回本地临时文件名
            String imgFile = ImgUtils.getImgFile(fileInputStream);
            ocrRecord.setLocalFilePath(imgFile);
            ocrRecord.setToken(token);
            // 进行识别
            T result = this.doRecognition(ocrRecord);
            ocrRecord = this.transfer(ocrRecord, result);
            // 删除文件
            File localFile = new File(imgFile);
            if (localFile.exists()) localFile.delete();
        }
        return ocrRecord;
    }


    private InputStream getFileInputStream(String fileId) {
        log.info("下载文件开始：" + fileId);
        GlobalPlatformApi globalPlatformApi = SpringUtil.getBean(GlobalPlatformApi.class);
        Response response = globalPlatformApi.download(fileId);
        Response.Body body = response.body();
        try {
            InputStream inputStream = body.asInputStream();
            log.info("下载文件结束：" + fileId);
            return inputStream;
        } catch (IOException e) {
            throw new RuntimeException("文件流获取失败");
        }
    }

}
