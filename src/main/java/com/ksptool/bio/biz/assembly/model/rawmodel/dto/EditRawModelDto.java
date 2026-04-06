package com.ksptool.bio.biz.assembly.model.rawmodel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditRawModelDto {

    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID")
    private Long id;

    @NotNull(message = "输出方案ID不能为空")
    @Schema(description = "输出方案ID")
    private Long outputSchemaId;

    @NotBlank(message = "字段名不能为空")
    @Size(max = 255, message = "字段名长度不能超过255")
    @Schema(description = "字段名")
    private String name;

    @NotBlank(message = "数据类型不能为空")
    @Size(max = 255, message = "数据类型长度不能超过255")
    @Schema(description = "数据类型")
    private String dataType;

    @Schema(description = "长度")
    private Integer length;

    @NotNull(message = "必填不能为空")
    @Range(min = 0, max = 1, message = "必填值只能为0或1")
    @Schema(description = "必填 0:否 1:是")
    private Integer require;

    @NotNull(message = "是否主键不能为空")
    @Range(min = 0, max = 1, message = "是否主键值只能为0或1")
    @Schema(description = "是否主键 0:否 1:是")
    private Integer pk;

    @Size(max = 255, message = "备注长度不能超过255")
    @Schema(description = "备注")
    private String remark;

    @NotNull(message = "排序不能为空")
    @Schema(description = "排序")
    private Integer seq;

}
