package com.ksptool.bio.biz.assembly.model.opschema.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ExecuteOpSchemaDto {

    @Schema(description = "输出方案ID")
    @NotNull(message = "输出方案ID不能为空")
    private Long opSchemaId;

    @Schema(description = "蓝图SHA256列表")
    @NotNull(message = "蓝图SHA256列表不能为空")
    private List<String> sha256Hexs;

}
