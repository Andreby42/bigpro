package com.thc.platform.modules.ocr.util;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.ocr.AipOcr;
import com.google.common.collect.ImmutableMap;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.external.api.GlobalPlatformApi;
import com.thc.platform.modules.ocr.bean.baidu.OcrResultDO;
import com.thc.platform.modules.ocr.util.constant.OcrConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class BaiDuRecognitionUtils {

    private static final String APP_ID_KEY = "BAI_DU_APP_ID_KEY";                                           // 百度appId
    private static final String API_KEY_KEY = "BAI_DU_API_KEY_KEY";                                         // 百度apiKey
    private static final String SECRET_KEY_KEY = "BAI_DU_SECRET_KEY_KEY";                                   // 百度密钥
    private static final String CLASSIFIER_ID_KEY_INSPECTION = "BAI_DU_CLASSIFIER_ID_KEY_INSPECTION";       // 百度检验分类器ID
    private static final String CLASSIFIER_ID_KEY_EXAMINE = "BAI_DU_CLASSIFIER_ID_KEY_EXAMINE";             // 百度检查分类器ID

    private static final ImmutableMap<Integer, String> CLASSIFIER_ID_KEY_MAP = ImmutableMap.of(
            OcrConstants.ImgType.INSPECTION, CLASSIFIER_ID_KEY_INSPECTION,
            OcrConstants.ImgType.EXAMINE, CLASSIFIER_ID_KEY_EXAMINE
    );

    @Resource
    private GlobalPlatformApi globalPlatformApi;

    private AipOcr getApiClient(String token) {
        Map<String, String> payload = new HashMap<>();
        // 获取 api_id
        payload.put("key", APP_ID_KEY);
        Api<List<Map<String, String>>> appIdKeyValueResp = globalPlatformApi.getKeyValueInfoList(payload, token);
        String appId = this.getAppValue(appIdKeyValueResp);
        // 获取 api_key
        payload.put("key", API_KEY_KEY);
        appIdKeyValueResp = globalPlatformApi.getKeyValueInfoList(payload, token);
        String apiKey = this.getAppValue(appIdKeyValueResp);
        // 获取secret_key
        payload.put("key", SECRET_KEY_KEY);
        appIdKeyValueResp = globalPlatformApi.getKeyValueInfoList(payload, token);
        String secretKey = this.getAppValue(appIdKeyValueResp);
        if (StringUtils.isNotEmpty(appId) && StringUtils.isNotEmpty(apiKey) && StringUtils.isNotEmpty(secretKey)) {
            AipOcr aipOcr = new AipOcr(appId, apiKey, secretKey);
            log.info("******** 远程获取百度参数: {} ********", JSON.toJSON(aipOcr));
            return aipOcr;
        }
        throw new RuntimeException("系统配置中没有百度AI对应项");
    }

    private String getAppValue(Api<List<Map<String, String>>> appIdKeyValueResp) {
        if (null != appIdKeyValueResp && appIdKeyValueResp.isSuccess()) {
            List<Map<String, String>> data = appIdKeyValueResp.getData();
            if (CollectionUtils.isNotEmpty(data)) {
                return data.get(0).get("value");
            }
        }
        return null;
    }

    /**
     * 获取分类器ID
     * @param fileType
     * @param token
     * @return
     */
    private String getClassifierId(Integer fileType, String token) {
        Map<String, String> payload = new HashMap<>();
        payload.put("key", CLASSIFIER_ID_KEY_MAP.get(fileType));
        Api<List<Map<String, String>>> classifierResp = globalPlatformApi.getKeyValueInfoList(payload, token);
        return this.getAppValue(classifierResp);
    }

    public OcrResultDO recognition(String filePath, Integer fileType, String token) {
        // 获取客户端
        AipOcr client = this.getApiClient(token);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);

        // 根据图片类型获取分类器ID
        String classifierId = this.getClassifierId(fileType, token);
        if (StringUtils.isEmpty(classifierId)) throw new RuntimeException("分类器ID为空");
        log.info("******** 分类器ID: {} ********", classifierId);
        // 调用接口
        HashMap<String, String> option = new HashMap<>();
        option.put("classifierId", classifierId);
        JSONObject res = client.custom(filePath, option);
        return JSON.parseObject(res.toString(2), OcrResultDO.class);
    }

}
