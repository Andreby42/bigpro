package com.thc.platform.modules.apppush.dto.ios;

import com.thc.platform.common.util.BEUtil;
import com.thc.platform.common.util.StringUtil;

import lombok.Data;

@Data
public class DictionaryAlertIn {

	// 通知标题
	private String title;
	// 通知文本消息字符串
	private String body;
	
	// (用于多语言支持）对于标题指定执行按钮所使用的Localizable.strings,仅支持iOS8.2以上版本
//	private String titleLocKey;
	// 对于标题，如果loc-key中使用的是占位符，则在loc-args中指定各参数,仅支持iOS8.2以上版本
//	private List<String> titleLocArgs = new ArrayList<>();
	
	// (用于多语言支持）指定执行按钮所使用的Localizable.strings
//	private String actionLocKey;
	// (用于多语言支持）指定Localizable.strings文件中相应的key
//	private String locKey;
	// 如果loc-key中使用的是占位符，则在loc-args中指定各参数
//	private List<String> locArgs = new ArrayList<>();
	// 指定启动界面图片名
//	private String launchImage;
	
	// 设置子标题，仅支持iOS8.2以上版本
//	private String subTitle;
	// 设置当前本地化文件中的子标题字符串的关键字,仅支持iOS8.2以上版本
//	private String subTitleLocKey;
	// 设置当前本地化子标题内容中需要置换的变量参数 ，仅支持iOS8.2以上版本
//	private List<String> subTitleLocArgs = new ArrayList<>();
	// 合并分组时简介信息，仅支持iOS12.0以上版本
//	private String summaryArg;
	// 合并分组时单条消息中包含数字，仅支持iOS12.0以上版本
//	private Integer summaryArgCount = Integer.MIN_VALUE;
		
	public void validate() {
		if(StringUtil.isEmpty(title) && StringUtil.isEmpty(body))
			throw BEUtil.illegalFormat("dictionaryAlert's title and body both empty.");
	}
	
}
