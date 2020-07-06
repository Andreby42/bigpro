package com.thc.platform.modules.sms.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.dto.PageOut;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.BaseEntityUtil;
import com.thc.platform.common.util.ShortUUID;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.external.dto.SysUserDto;
import com.thc.platform.external.service.GlobalPlatformService;
import com.thc.platform.modules.sms.dao.SmsSendNodeDao;
import com.thc.platform.modules.sms.dto.SmsSendNodeListPageIn;
import com.thc.platform.modules.sms.dto.SmsSendNodeSaveIn;
import com.thc.platform.modules.sms.entity.SmsSendNodeEntity;

@Service
public class SmsSendNodeService extends ServiceImpl<SmsSendNodeDao, SmsSendNodeEntity> {

	@Autowired
	private GlobalPlatformService globalPlatformService;
	
	public String save(SmsSendNodeSaveIn in) {
		SysUserDto sysUser = globalPlatformService.getCurrentUser();
		if(sysUser == null)
			throw BEUtil.failNormal("获取登录用户信息异常");
		
		if(StringUtil.isEmpty(in.getId()))
			return create(in, sysUser);
		else
			return update(in, sysUser);
	}
	
	private String create(SmsSendNodeSaveIn in, SysUserDto sysUser) {
		SmsSendNodeEntity existedEntity = getByCode(in.getCode());
		if(existedEntity != null)
			throw BEUtil.failNormal("节点编码已存在，请更换编码，重新提交");
		
		SmsSendNodeEntity entity = new SmsSendNodeEntity();
		entity.setId(ShortUUID.uuid());
		entity.setCode(in.getCode());
		entity.setName(in.getName());
		BaseEntityUtil.setCreateAndModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName());
		
		this.save(entity);
		return entity.getId();
	}
	
	private String update(SmsSendNodeSaveIn in, SysUserDto sysUser) {
		SmsSendNodeEntity existedEntity = getById(in.getId());
		
		if(existedEntity == null)
			throw BEUtil.failNormal("编辑发送节点不存在");
		
		SmsSendNodeEntity sameCodeEntity = getByCode(in.getCode());
		if(sameCodeEntity != null && 
				!sameCodeEntity.getId().equals(in.getId()))
			throw BEUtil.failNormal("节点编码已存在，请更换编码，重新提交");
		
		SmsSendNodeEntity entity = new SmsSendNodeEntity();
		entity.setId(in.getId());
		entity.setCode(in.getCode());
		entity.setName(in.getName());
		
		BaseEntityUtil.setModifyBaseEntityInfo(entity, sysUser.getId(), sysUser.getName());
		updateById(entity);
		return entity.getId();
	}
	
	public SmsSendNodeEntity getByCode(String code) {
		return baseMapper.selectOne(
					Wrappers.<SmsSendNodeEntity>lambdaQuery()
						.eq(SmsSendNodeEntity::getCode, code)
				);
	}
	
	public PageOut<SmsSendNodeEntity> listPage(SmsSendNodeListPageIn in) {
		 LambdaQueryWrapper<SmsSendNodeEntity> wrapper = Wrappers.lambdaQuery();
		 if(StringUtil.isNotEmpty(in.getName()))
			 wrapper.like(SmsSendNodeEntity::getName, in.getName());
		 
		 if(StringUtil.isNotEmpty(in.getCode()))
			 wrapper.like(SmsSendNodeEntity::getCode, in.getCode());
		 
		 Integer totalCount = baseMapper.selectCount(wrapper);
		 wrapper.last("limit " + in.getOffset() + ", " + in.getPagesize());
		 List<SmsSendNodeEntity> items = baseMapper.selectList(wrapper);
		 
		 return new PageOut<SmsSendNodeEntity>(items, totalCount);
	}
	
}
