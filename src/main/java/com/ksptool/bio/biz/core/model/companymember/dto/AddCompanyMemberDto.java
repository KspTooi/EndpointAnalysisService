package com.ksptool.bio.biz.core.model.companymember.dto;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCompanyMemberDto {

    @NotNull(message = "公司ID不能为空")
    @Schema(description = "公司ID")
    private Long companyId;

    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;

    @Range(min = 0, max = 1, message = "职务必须在0和1之间")
    @NotNull(message = "职务不能为空")
    @Schema(description = "职务 0:CEO 1:成员")
    private Integer role;

}

