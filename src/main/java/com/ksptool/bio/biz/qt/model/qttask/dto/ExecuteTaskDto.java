package com.ksptool.bio.biz.qt.model.qttask.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExecuteTaskDto {

    @Schema(description = "任务ID")
    @NotNull(message = "任务ID不能为空")
    private Long id;

    @Schema(description = "调用参数JSON")
    private String targetParam;

}
