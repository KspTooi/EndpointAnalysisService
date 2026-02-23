package com.ksptool.bio.biz.qt.model.qttaskgroup.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Length;
import jakarta.validation.constraints.NotBlank;

@Getter
@Setter
public class EditQtTaskGroupDto {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @NotBlank(message = "分组名不能为空")
    @Length(max = 80, message = "分组名长度不能超过80个字符")
    @Schema(description = "分组名")
    private String name;

    @Length(max = 1000, message = "分组备注长度不能超过1000个字符")
    @Schema(description = "分组备注")
    private String remark;


}

