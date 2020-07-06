package com.thc.platform.modules.help.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.thc.platform.common.entity.CreateBaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 页面与视频关联实体
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper=false)
@TableName("help_page_video_rel")
public class HelpPageVideoRelEntity extends CreateBaseEntity {

	public static final int DELETE_STATUS_NO = 0;
	public static final int DELETE_STATUS_YES = 1;
	
	@TableId(type = IdType.INPUT)
	private String id;
	// 关联帮忙页面ID
	private String pageId;
	// 关联视频ID
	private String videoId;
	// 删除状态
	private Integer deleteStatus = DELETE_STATUS_NO;
	
	public HelpPageVideoRelEntity() {}
	
	public HelpPageVideoRelEntity(String id, String pageId, String videoId) {
		this.id = id;
		this.pageId = pageId;
		this.videoId = videoId;
		deleteStatus = DELETE_STATUS_NO;
	}
	
	
}
