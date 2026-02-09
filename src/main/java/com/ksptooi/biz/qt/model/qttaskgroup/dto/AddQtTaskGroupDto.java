package com.ksptooi.biz.qt.model.qttaskgroup.dto;

import org.hibernate.validator.constraints.Length;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddQtTaskGroupDto {


    @NotBlank(message = "分组名不能为空")
    @Length(max = 80, message = "分组名长度不能超过80个字符")
    @Schema(description = "分组名")
    private String name;

    @Length(max = 1000, message = "分组备注长度不能超过1000个字符")
    @Schema(description = "分组备注")
    private String remark;

}

