package com.ksptool.bio.biz.core.model.companymember.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FireCompanyMemberDto {

    @NotNull(message = "公司ID不能为空")
    @Schema(description = "公司ID")
    private Long companyId;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

}

