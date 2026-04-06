package com.ksptool.bio.biz.assembly.model.opschema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreviewOpBluePrintDto {

    @Schema(description = "输出方案ID")
    @NotNull(message = "输出方案ID不能为空")
    private Long opSchemaId;
    
    @Schema(description = "蓝图SHA256")
    @NotBlank(message = "蓝图SHA256不能为空")
    private String sha256Hex;

}
