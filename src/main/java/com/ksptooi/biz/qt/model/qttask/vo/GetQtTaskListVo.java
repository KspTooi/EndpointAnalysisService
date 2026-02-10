package com.ksptooi.biz.qt.model.qttask.vo;

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


}

