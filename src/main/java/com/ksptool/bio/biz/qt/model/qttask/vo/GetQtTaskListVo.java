package com.ksptool.bio.biz.qt.model.qttask.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetQtTaskListVo {

    @Schema(description = "任务ID")
    private Long id;

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

    @Schema(description = "任务有效期截止")
    private LocalDateTime expireTime;

    @Schema(description = "是否已过有效期 0:未过 1:已过")
    private Integer isExpired;

    @Schema(description = "上次状态 0:成功 1:异常")
    private Integer lastExecStatus;

    @Schema(description = "上次开始时间")
    private LocalDateTime lastStartTime;

    @Schema(description = "上次结束时间")
    private LocalDateTime lastEndTime;

    @Schema(description = "0:正常 1:暂停 2:暂停(异常)")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;


}

