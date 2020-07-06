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
import com.thc.platform.modules.sms.dao.YtxAccountDao;
import com.thc.platform.modules.sms.dto.YtxAccountListIn;
import com.thc.platform.modules.sms.dto.YtxAccountSaveIn;
import com.thc.platform.modules.sms.entity.YtxAccountEntity;

/** 云通讯账户服务 */
@Service
public class YtxAccountService extends ServiceImpl<YtxAccountDao, YtxAccountEntity> {

	@Autowired
	private GlobalPlatformService globalPlatformService;
	
	/** 保存 */
	public void save(YtxAccountSaveIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		Date currentTime = new Date();
		YtxAccountEntity entity = new YtxAccountEntity();
		
		if(StringUtil.isEmpty(in.getId())) {
			entity.setId(ShortUUID.uuid());
			entity.setCreatorId(sysUser.getId());
			entity.setCreatorName(sysUser.getName());
			entity.setCreateTime(currentTime);
		} else {
			entity.setId(in.getId());
		}
		
		entity.setAccountSid(in.getAccountSid())
			.setName(in.getName())
			.setAuthToken(in.getAuthToken())
			.setUrl(in.getUrl())
			.setPort(in.getPort())
			.setNotes(in.getNotes())
			.setModifyUserId(sysUser.getId())
			.setModifyUserName(sysUser.getName())
			.setModifyTime(currentTime);
		
		this.saveOrUpdate(entity);
	}
	
	/** 分页查询  */
	public PageOut<YtxAccountEntity> listPage(YtxAccountListIn in) {
	
		LambdaQueryWrapper<YtxAccountEntity> wrapper = Wrappers.lambdaQuery();
		
		if(StringUtil.isNotEmpty(in.getKeyword())) {
			wrapper.like(YtxAccountEntity::getName, in.getKeyword());
//				.or()
//				.like(YtxAccountEntity::getNotes, in.getKeyword());
		}
		
		Integer totalCount = baseMapper.selectCount(wrapper);
		
		wrapper.last("limit " + in.getOffset() + ", " + in.getPagesize());
		
		List<YtxAccountEntity> items = baseMapper.selectList(wrapper);
		
		return new PageOut<YtxAccountEntity>(items, totalCount);
	}
	
}
