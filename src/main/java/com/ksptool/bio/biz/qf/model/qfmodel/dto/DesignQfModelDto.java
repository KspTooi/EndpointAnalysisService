package com.ksptool.bio.biz.qf.model.qfmodel.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class DesignQfModelDto {

    @NotNull(message = "流程模型ID不能为空")
    @Schema(description = "流程模型ID")
    private Long id;

    @NotBlank(message = "BPMN XML不能为空")
    @Schema(description = "BPMN XML")
    private String bpmnXml;

}
