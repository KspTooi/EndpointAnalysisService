package com.ksptool.bio.biz.core.model.noticercd.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class GetUserNoticeRcdListVo {

    @Schema(description = "RCD主键ID")
    private Long id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "种类: 0公告, 1业务提醒, 2私信")
    private Integer kind;

    @Schema(description = "优先级: 0:低 1:中 2:高")
    private Integer priority;

    @Schema(description = "业务类型/分类")
    private String category;

    @Schema(description = "发送人姓名")
    private String senderName;

    @Schema(description = "跳转URL/路由地址")
    private String forward;

    @Schema(description = "动态参数 (JSON格式)")
    private String params;

    @Schema(description = "下发时间")
    private LocalDateTime createTime;
}

