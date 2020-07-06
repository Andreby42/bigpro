package com.thc.platform.common.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;

import com.alibaba.fastjson.JSON;

import lombok.Data;

/**
 * Bean 操作工具类
 */
public class BeanUtil {

	private static final Logger logger = LoggerFactory.getLogger(BeanUtil.class);
	
	/**
	 * 列表属性复制
	 * @param <S> 源Bean Class 类型
	 * @param <T> 目标Bean Class 类型
	 * @param srcObjList 源实例数据列表
	 * @param c 目标Bean Class
	 * @return 目标实例数据列表
	 */
	public static <S, T> List<T> listCopy(List<S> srcObjList, Class<T> c) {
		if(srcObjList == null || srcObjList.size() == 0)
			return new ArrayList<>();
		
		List<T> targetList = new ArrayList<>(srcObjList.size());
		for(S src : srcObjList) {
			T target = null;
			try {
				target = c.newInstance();
			} catch (Exception e) {
				logger.error("", e);
				continue;
			}
			
			BeanUtils.copyProperties(src, target);
			targetList.add(target);
		}
		return targetList;
	}
	
	@Data
	public static class Source {
		private String id;
		private String name;
		
		public Source(String id, String name) {
			this.id = id;
			this.name = name;
		}
		
	}
	
	@Data
	public static class Target {
		private String id;
		private String name;
		
	}
	
	public static void main(String[] args) {
		List<Source> srcList = new ArrayList<>(3);
		for(int i = 0; i < 3; i ++) 
			srcList.add(new Source("id-" + i, "name-" + i));
		
		List<Target> targetList = BeanUtil.listCopy(srcList, Target.class);
		
		System.out.println(JSON.toJSONString(targetList));
	}
}
