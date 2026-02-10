package com.ksptooi.biz.qttaskrcd.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Getter
@Setter
public class GetQtTaskRcdDetailsVo{

    @Schema(description="调度日志ID")
    private Long id;

    @Schema(description="任务ID")
    private Long taskId;

    @Schema(description="任务名")
    private String taskName;

    @Schema(description="分组名")
    private String groupName;

    @Schema(description="调用目标")
    private String target;

    @Schema(description="调用目标参数")
    private String targetParam;

    @Schema(description="调用目标返回内容(错误时为异常堆栈)")
    private String targetResult;

    @Schema(description="运行状态 0:正常 1:失败 2:超时 3:已调度")
    private Integer status;

    @Schema(description="运行开始时间")
    private LocalDateTime startTime;

    @Schema(description="运行结束时间")
    private LocalDateTime endTime;

    @Schema(description="耗时(MS)")
    private Integer costTime;

    @Schema(description="创建时间")
    private LocalDateTime createTime;

}

