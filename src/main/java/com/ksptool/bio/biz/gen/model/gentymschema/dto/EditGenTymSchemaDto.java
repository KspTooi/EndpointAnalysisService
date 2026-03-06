package com.ksptool.bio.biz.gen.model.gentymschema.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditGenTymSchemaDto {

    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID")
    private Long id;

    @NotBlank(message = "方案名称不能为空")
    @Size(max = 32, min = 1, message = "方案名称长度必须在1-32个字符之间")
    @Schema(description = "方案名称")
    private String name;

    @NotBlank(message = "方案编码不能为空")
    @Size(max = 32, min = 1, message = "方案编码长度必须在1-32个字符之间")
    @Schema(description = "方案编码")
    private String code;

    @NotBlank(message = "映射源不能为空")
    @Size(max = 32, min = 1, message = "映射源长度必须在1-32个字符之间")
    @Schema(description = "映射源")
    private String mapSource;

    @NotBlank(message = "映射目标不能为空")
    @Size(max = 32, min = 1, message = "映射目标长度必须在1-32个字符之间")
    @Schema(description = "映射目标")
    private String mapTarget;

    @NotNull(message = "类型数量不能为空")
    @Range(min = 0, message = "类型数量不能小于0")
    @Schema(description = "类型数量")
    private Integer typeCount;

    @NotBlank(message = "默认类型不能为空")
    @Size(max = 80, min = 1, message = "默认类型长度必须在1-80个字符之间")
    @Schema(description = "默认类型")
    private String defaultType;

    @NotNull(message = "排序不能为空")
    @Range(min = 0, message = "排序不能小于0")
    @Schema(description = "排序")
    private Integer seq;

    @Size(max = 65535, message = "备注长度不能超过65535个字符")
    @Schema(description = "备注")
    private String remark;

}
