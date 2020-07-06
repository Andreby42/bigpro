package com.thc.platform.modules.apppush.util;

import com.gexin.rp.sdk.base.impl.Target;
import com.thc.platform.common.util.StringUtil;
import com.thc.platform.modules.apppush.dto.TargetIn;

public class GTUtil {

	/** 类型：立即启动APP（不推荐使用，影响客户体验） */
	public static final int TYPE_START_APP_NOW = 1;
	/** 类型：客户端收到消息后需要自行处理 */
	public static final int TYPE_CUSTOM_PROCESS = 2;
	
	public static Target getTarget(TargetIn in, String gtAppId) {
		
		Target target = new Target();
        target.setAppId(gtAppId);
        
        if(StringUtil.isNotEmpty(in.getClientId()))
        	target.setClientId(in.getClientId());
        
        if(StringUtil.isNotEmpty(in.getAlias()))
        	target.setAlias(in.getAlias());
        
        return target;
	}
	
}
