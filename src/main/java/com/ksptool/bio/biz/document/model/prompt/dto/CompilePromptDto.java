package com.ksptool.bio.biz.document.model.prompt.dto;

import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompilePromptDto {

    @Schema(description = "提示词ID")
    @NotNull(message = "提示词ID不能为空")
    private Long id;

    @Schema(description = "参数槽位")
    @NotNull(message = "参数槽位不能为空")
    private Map<String, String> params;

}
