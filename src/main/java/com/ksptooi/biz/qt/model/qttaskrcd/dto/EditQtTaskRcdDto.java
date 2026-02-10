package com.ksptooi.biz.qt.model.qttaskrcd.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.LocalDateTime;

@Getter
@Setter
public class EditQtTaskRcdDto {

    @Schema(description = "调度日志ID")
    @NotNull(message = "调度日志ID不能为空")
    private Long id;

    @Schema(description = "任务ID")
    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @Schema(description = "任务名")
    @NotBlank(message = "任务名不能为空")
    @Size(max = 80, message = "任务名长度不能超过80个字符")
    private String taskName;

    @Schema(description = "分组名")
    @Size(max = 80, message = "分组名长度不能超过80个字符")
    private String groupName;

    @Schema(description = "调用目标")
    @NotBlank(message = "调用目标不能为空")
    @Size(max = 1000, message = "调用目标长度不能超过1000个字符")
    private String target;

    @Schema(description = "调用目标参数")
    private String targetParam;

    @Schema(description = "调用目标返回内容(错误时为异常堆栈)")
    private String targetResult;

    @Schema(description = "运行状态 0:正常 1:失败 2:超时 3:已调度")
    @NotNull(message = "运行状态不能为空")
    @Range(min = 0, max = 3, message = "运行状态必须在0-3之间")
    private Integer status;

    @Schema(description = "运行开始时间")
    @NotNull(message = "运行开始时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "运行结束时间")
    private LocalDateTime endTime;

    @Schema(description = "耗时(MS)")
    private Integer costTime;

}

