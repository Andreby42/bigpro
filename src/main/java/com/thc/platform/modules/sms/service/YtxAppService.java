package com.thc.platform.modules.sms.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.sms.dao.YtxAppDao;
import com.thc.platform.modules.sms.dto.YtxAppChgWhiteStatusIn;
import com.thc.platform.modules.sms.dto.YtxAppListIn;
import com.thc.platform.modules.sms.dto.YtxAppSaveIn;
import com.thc.platform.modules.sms.entity.YtxAppEntity;

/** 云通讯应用服务 */
@Service
public class YtxAppService extends ServiceImpl<YtxAppDao, YtxAppEntity> {

	public static final String ACCOUNT_ID_DEFAULT = "default";
	
	@Autowired
	private GlobalPlatformService globalPlatformService;
	
	/** 保存 */
	public void save(YtxAppSaveIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		Date currentTime = new Date();
		
		YtxAppEntity entity = new YtxAppEntity();
		
		if(StringUtil.isEmpty(in.getId())) {
			entity.setId(ShortUUID.uuid());
			entity.setCreatorId(sysUser.getId());
			entity.setCreatorName(sysUser.getName());
			entity.setCreateTime(currentTime);
		} else {
			entity.setId(in.getId());
		}
		
		entity.setAppId(in.getAppId())
			.setAccountId(ACCOUNT_ID_DEFAULT)
			.setWhiteListStatus(in.getWhiteListStatus())
			.setName(in.getName())
			.setNotes(in.getNotes())
			.setModifyUserId(sysUser.getId())
			.setModifyUserName(sysUser.getName())
			.setModifyTime(currentTime);
	
		this.saveOrUpdate(entity);
		
	}
	
	/** 按账户ID统计数量  */
	public Integer countByAccountId(String accountId) {
		return baseMapper.selectCount(
				Wrappers.<YtxAppEntity>lambdaQuery()
					.eq(YtxAppEntity::getAccountId, accountId));
	}
	
	/** 分页查询  */
	public PageOut<YtxAppEntity> listPage(YtxAppListIn in) {
		
		LambdaQueryWrapper<YtxAppEntity> wrapper = Wrappers.lambdaQuery();
		
		if(StringUtil.isNotEmpty(in.getKeyword()))
			wrapper.like(YtxAppEntity::getName, in.getKeyword());
		
		if(StringUtil.isNotEmpty(in.getAccountId()))
			wrapper.like(YtxAppEntity::getAccountId, in.getAccountId());
			
		Integer totalCount = baseMapper.selectCount(wrapper);
		
		wrapper.last("limit " + in.getOffset() + ", " + in.getPagesize());
		
		List<YtxAppEntity> items = baseMapper.selectList(wrapper);
		
		return new PageOut<YtxAppEntity>(items, totalCount);
	}
	
	/** 更改白名单功能开通状态  */
	public void changeWhiteListStatus(YtxAppChgWhiteStatusIn in) {
		YtxAppEntity entity = new YtxAppEntity();
		
		entity.setId(in.getId());
		entity.setWhiteListStatus(in.getWhiteListStatus());
		
		baseMapper.updateById(entity);
	}
	
}
