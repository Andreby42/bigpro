package com.thc.platform.external.api;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.thc.platform.common.protocol.Api;
import com.thc.platform.external.dto.AccountInfoIn;
import com.thc.platform.external.dto.AccountInfoOut;
import com.thc.platform.external.dto.AppConfigIn;
import com.thc.platform.external.dto.AppConfigOut;
import com.thc.platform.external.dto.BillListAndBillDetailListDto;
import com.thc.platform.external.dto.ListTenantByTypesIn;
import com.thc.platform.external.dto.ServicePriceIn;
import com.thc.platform.external.dto.ServicePriceOut;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.dto.TenantDto;
import com.thc.platform.external.dto.TenantInternalConfigDto;
import com.thc.platform.external.dto.TenantListOut;
import com.thc.platform.external.dto.TenantQryByIdsIn;

import feign.Response;

@FeignClient("global-platform")
public interface GlobalPlatformApi {

    @GetMapping("/sys/user/getCurrentUser")
    Api<SysUserDto> getCurrentUser(@RequestHeader("x-access-token") String token);

    /**
     * 获取机构的微信公众号&小程序配置信息
     * http://yapi.everjiankang.com/project/80/interface/api/20584
     *
     * @param appConfigIn 查询条件
     * @return
     */
    @GetMapping("/organizationAppconfig/getOrganizationAppconfigs")
    Api<List<AppConfigOut>> getOrgAppConfigs(@RequestBody AppConfigIn appConfigIn);

    /**
     * 获取租户和租户配置信息（按ID查询）
     * http://yapi.everjiankang.com/project/80/interface/api/20199
     *
     * @return
     */
    @GetMapping("/internal/tenant/getTenantsWithConfigs")
    Api<TenantInternalConfigDto> getTenantsWithConfigs(@RequestParam("tenantId") Integer tenantId);

    /**
     * 获取租户账户余额
     */
    @PostMapping("/internal/tcm/manage/account/getTenantAccountBalance")
    Api<AccountInfoOut> getTenantAccountBalance(@RequestBody AccountInfoIn in);

    /**
     * 获取服务价格
     */
    @PostMapping("/internal/tcm/manage/account/getPriceByTenantIdAndType")
    Api<ServicePriceOut> getPriceByTenantIdAndType(@RequestBody ServicePriceIn in);

    /**
     * 推送账单明细和账单的列表
     */
    @PostMapping("/internal/tcm/manage/account/saveBillAndBillDetail")
    Api<Object> saveBillAndBillDetail(@RequestBody BillListAndBillDetailListDto in);

    @PostMapping("/thctenant/getByIds")
    Api<List<TenantDto>> getTenantByIds(@RequestBody TenantQryByIdsIn in, @RequestHeader("x-access-token") String token);

    @PostMapping("/keyValueInfo/getKeyValueInfos/")
    Api<List<Map<String, String>>> getKeyValueInfoList(@RequestBody Map<String, String> payload,
                                                       @RequestHeader("x-access-token") String token);

    /**
     * 文件下载
     */
    @RequestMapping(value = "/phoneMapping/download", method = RequestMethod.GET)
    Response download(@RequestParam("fileId") String fileId);
    
    @PostMapping("/cm/listCustomByTypes")
    Api<List<TenantListOut>> listTenantByTypes(@RequestBody ListTenantByTypesIn in
    		, @RequestHeader("x-access-token") String token);
    
}
