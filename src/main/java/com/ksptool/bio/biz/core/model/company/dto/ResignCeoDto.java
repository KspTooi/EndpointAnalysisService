package com.ksptool.bio.biz.core.model.company.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResignCeoDto {

    @NotNull(message = "公司ID不能为空")
    @Schema(description = "公司ID")
    private Long companyId;

    @NotNull(message = "CEO移交用户ID不能为空")
    @Schema(description = "CEO移交用户ID")
    private Long newCeoUserId;

}

