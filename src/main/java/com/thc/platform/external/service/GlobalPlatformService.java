package com.thc.platform.external.service;

import com.thc.platform.external.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.thc.platform.common.protocol.Api;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.TokenUtil;
import com.thc.platform.external.api.GlobalPlatformApi;

import java.math.BigDecimal;
import java.util.List;

@Service
public class GlobalPlatformService {

	private static Logger logger = LoggerFactory.getLogger(GlobalPlatformService.class);
	
	@Autowired
	private GlobalPlatformApi api;
	
	public SysUserDto getCurrentUser() {
		
		Api<SysUserDto> apiRes = null;
		try {
			apiRes = api.getCurrentUser(TokenUtil.getToken());
		} catch (Exception e) {
			throw BEUtil.failNormal("调用全局平台接口错误：" + e.getMessage());
		}
		
		if(apiRes.isSuccess()) {
			SysUserDto sysUser = apiRes.getData();
			if(sysUser == null)
				throw BEUtil.failNormal("未查询到登录用户信息");
			else 
				return sysUser;
		} else {
			logger.error("获取当前登录用户失败，全局平台响应数据：" + JSON.toJSONString(apiRes));
			throw BEUtil.failNormal("获取当前登录用户错误：" + apiRes.getMsg());
		}
		
	}

	/**
	 * 获取微信配置信息
	 *
	 * @return
	 */
	public List<AppConfigOut> getTenantMiniAppConfig(AppConfigIn appConfigIn) {
		Api<List<AppConfigOut>> result = api.getOrgAppConfigs(appConfigIn);
		logger.debug("获取微信配置信息 request [{}] response : [{}]", JSON.toJSONString(appConfigIn), result);
		if(result.isSuccess()) {
			return result.getData();
		} else {
			logger.error("获取微信配置信息失败，全局平台响应数据：" + JSON.toJSONString(result));
			throw BEUtil.failNormal("获取微信配置信息失败：" + result.getMsg());
		}
	}
	/**
	 * 获取租户账户信息
	 */
	public AccountInfoOut getTenantAccountBalance(AccountInfoIn in){
		Api<AccountInfoOut> result = api.getTenantAccountBalance(in);
		logger.debug("获取账户信息 request [{}] response : [{}]", JSON.toJSONString(in), result);
		if(result.isSuccess()) {
			return result.getData();
		} else {
			logger.error("获取账户信息失败，全局平台响应数据：" + JSON.toJSONString(result));
			throw BEUtil.failNormal("获取账户信息失败：" + result.getMsg());
		}
	}

	/**
	 * 获取服务价格:短信、快递
	 */
	public BigDecimal getPriceByTenantIdAndType(ServicePriceIn in){
		Api<ServicePriceOut> result = api.getPriceByTenantIdAndType(in);
		logger.debug("获取服务价格信息 request [{}] response : [{}]", JSON.toJSONString(in), result);
		if(result.isSuccess()) {
			return result.getData().getPrice();
		} else {
			logger.error("获取服务价格信息失败，全局平台响应数据：" + JSON.toJSONString(result));
			throw BEUtil.failNormal("获取服务价格信息失败：" + result.getMsg());
		}
	}
	/**
     * 推送账单明细和账单的列表
     */
    public void saveBillAndBillDetail(BillListAndBillDetailListDto in) {
        try {
            Api<Object> result = api.saveBillAndBillDetail(in);
            if (result.isSuccess()) {
                logger.debug("推送账单成功 request [{}] response : [{}]", JSON.toJSONString(in), result);
            } else {
                throw BEUtil.failNormal("推送账单失败：" + result.getMsg());
            }
        } catch (Exception e) {
            logger.error("推送账单失败{0}", e);
            throw BEUtil.failNormal("推送账单失败!");
        }
    }

}
