package com.ksptooi.biz.qt.model.qttask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetQtTaskDetailsVo {

    @Schema(description = "任务ID")
    private Long id;

    @Schema(description = "任务分组ID")
    private Long groupId;

    @Schema(description = "任务分组名")
    private String groupName;

    @Schema(description = "任务名")
    private String name;

    @Schema(description = "0:本地BEAN 1:远程HTTP")
    private Integer kind;

    @Schema(description = "CRON表达式")
    private String cron;

    @Schema(description = "调用目标(BEAN代码或HTTP地址)")
    private String target;

    @Schema(description = "调用参数JSON")
    private String targetParam;

    @Schema(description = "请求方法")
    private String reqMethod;

    @Schema(description = "并发执行 0:允许 1:禁止")
    private Integer concurrent;

    @Schema(description = "过期策略 0:放弃执行 1:立即执行 2:全部执行")
    private Integer misfirePolicy;

    @Schema(description = "任务有效期截止")
    private LocalDateTime expireTime;

    @Schema(description = "0:正常 1:暂停")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}

