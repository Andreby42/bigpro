package com.thc.platform.modules.help.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thc.platform.common.util.BEUtil;
import com.thc.platform.modules.help.dao.HelpWidgetMenuDao;
import com.thc.platform.modules.help.dto.HelpWidgetMenuTreeOut;
import com.thc.platform.modules.help.entity.HelpVerEntity;
import com.thc.platform.modules.help.entity.HelpWidgetMenuEntity;

@Service
public class HelpWidgetMenuService extends ServiceImpl<HelpWidgetMenuDao, HelpWidgetMenuEntity> {

	@Autowired
	private HelpVerService helpVerService;
	
	public HelpWidgetMenuTreeOut loadMenuTree(String helpId, String ver) {
		HelpVerEntity helpVerEntity = helpVerService.getByHelpIdAndVer(helpId, ver);
		if (helpVerEntity == null)
			throw BEUtil.failNormal("帮助版本不存在");

		List<HelpWidgetMenuTreeOut.MenuNodeOut> menuNodeOuts = new ArrayList<>();
		List<HelpWidgetMenuEntity> menuEntities = getRootByHelpVerId(helpVerEntity.getId());
		for (HelpWidgetMenuEntity entity : menuEntities)
			menuNodeOuts.add(loadChild(entity));

		HelpWidgetMenuTreeOut out = new HelpWidgetMenuTreeOut();
		out.setHelpId(helpVerEntity.getHelpId());
		out.setVer(helpVerEntity.getVer());
		out.setMenus(menuNodeOuts);

		return out;
	}

	private List<HelpWidgetMenuEntity> getRootByHelpVerId(String helpVerId) {
		LambdaQueryWrapper<HelpWidgetMenuEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HelpWidgetMenuEntity::getHelpVerId, helpVerId)
			.eq(HelpWidgetMenuEntity::getRootId, HelpWidgetMenuEntity.ROOT_ID);

		return baseMapper.selectList(wrapper);
	}

	public List<HelpWidgetMenuEntity> getByRootId(String rootId, String helpVerId) {
		LambdaQueryWrapper<HelpWidgetMenuEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HelpWidgetMenuEntity::getRootId, rootId)
			.eq(HelpWidgetMenuEntity::getHelpVerId, helpVerId);

		return baseMapper.selectList(wrapper);
	}
	
	public HelpWidgetMenuEntity getByMenuIdAndHelpVerId(String menuId, String helpVerId) {
		LambdaQueryWrapper<HelpWidgetMenuEntity> wrapper = Wrappers.lambdaQuery();
		wrapper.eq(HelpWidgetMenuEntity::getMenuId, menuId)
			.eq(HelpWidgetMenuEntity::getHelpVerId, helpVerId);

		return baseMapper.selectOne(wrapper);
	}

	private HelpWidgetMenuTreeOut.MenuNodeOut loadChild(HelpWidgetMenuEntity entity) {
		HelpWidgetMenuTreeOut.MenuNodeOut menuNodeOut = new HelpWidgetMenuTreeOut.MenuNodeOut();
		menuNodeOut.setId(entity.getMenuId());
		menuNodeOut.setName(entity.getName());

		List<HelpWidgetMenuEntity> children = getByRootId(entity.getMenuId(), entity.getHelpVerId());
		if (children != null && children.size() > 0) {
			List<HelpWidgetMenuTreeOut.MenuNodeOut> menuNodeOuts = new ArrayList<>();
			menuNodeOut.setNodes(menuNodeOuts);
			for (HelpWidgetMenuEntity child : children)
				menuNodeOuts.add(loadChild(child));
		}
		return menuNodeOut;
	}

}
