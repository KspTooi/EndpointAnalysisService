package com.ksptool.bio.biz.core.model.exceltemplate.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import io.swagger.v3.oas.annotations.media.Schema;
import org.hibernate.validator.constraints.Range;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditExcelTemplateDto {

    @NotNull(message = "模板ID不能为空")
    @Schema(description = "模板ID")
    private Long id;

    @Size(min = 2, max = 32, message = "模板名称长度必须在2-32个字符之间")
    @NotBlank(message = "模板名称不能为空")
    @Schema(description = "模板名称")
    private String name;

    @Size(min = 2, max = 32, message = "模板标识长度必须在2-32个字符之间")
    @NotBlank(message = "模板标识不能为空")
    @Schema(description = "模板标识 唯一")
    private String code;

    @Size(max = 1000, message = "模板备注长度不能超过1000个字符")
    @Schema(description = "模板备注")
    private String remark;

    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态只能在0-1之间")
    @Schema(description = "状态 0:启用 1:禁用")
    private Integer status;

}

