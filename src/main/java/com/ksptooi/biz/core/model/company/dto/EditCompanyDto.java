package com.ksptooi.biz.core.model.company.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EditCompanyDto {

    @Schema(description = "公司ID")
    @NotNull(message = "公司ID不能为空")
    private Long id;

    @Schema(description = "公司名")
    @Length(min = 2, max = 50, message = "公司名称长度必须在2-50个字符之间")
    @NotBlank(message = "公司名称不能为空")
    private String name;

    @Schema(description = "公司描述")
    @Length(max = 200, message = "公司描述长度不能超过200个字符")
    private String description;

}

