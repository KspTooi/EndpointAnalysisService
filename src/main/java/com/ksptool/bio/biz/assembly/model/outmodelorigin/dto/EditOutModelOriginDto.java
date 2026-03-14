package com.ksptool.bio.biz.assembly.model.outmodelorigin.dto;

import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditOutModelOriginDto {

    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID")
    private Long id;

    @NotNull(message = "输出方案ID不能为空")
    @Schema(description = "输出方案ID")
    private Long outputSchemaId;

    @NotBlank(message = "原始字段名不能为空")
    @Size(max = 255, message = "原始字段名长度不能超过255")
    @Schema(description = "原始字段名")
    private String name;

    @NotBlank(message = "原始数据类型不能为空")
    @Size(max = 255, message = "原始数据类型长度不能超过255")
    @Schema(description = "原始数据类型")
    private String kind;

    @Size(max = 255, message = "原始长度不能超过255")
    @Schema(description = "原始长度")
    private String length;

    @NotNull(message = "原始必填不能为空")
    @Range(min = 0, max = 1, message = "原始必填值只能为0或1")
    @Schema(description = "原始必填 0:否 1:是")
    private Integer require;

    @Size(max = 255, message = "原始备注长度不能超过255")
    @Schema(description = "原始备注")
    private String remark;

    @NotNull(message = "原始排序不能为空")
    @Schema(description = "原始排序")
    private Integer seq;

}
