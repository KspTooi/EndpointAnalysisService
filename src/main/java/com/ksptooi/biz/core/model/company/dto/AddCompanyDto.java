package com.ksptooi.biz.core.model.company.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddCompanyDto {

    @Schema(description = "公司名")
    @Length(min = 2, max = 50, message = "公司名称长度必须在2-50个字符之间")
    @NotBlank(message = "公司名称不能为空")
    private String name;

    @Schema(description = "公司描述")
    @Length(max = 200, message = "公司描述长度不能超过200个字符")
    private String description;

}

