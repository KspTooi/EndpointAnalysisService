package com.ksptool.bio.biz.assembly.model.rawmodel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRawModelDto {

    @NotNull(message = "输出方案ID不能为空")
    @Schema(description = "输出方案ID")
    private Long outputSchemaId;

}

