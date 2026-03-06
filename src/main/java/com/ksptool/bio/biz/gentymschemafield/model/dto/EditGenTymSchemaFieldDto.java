package com.ksptool.bio.biz.gentymschemafield.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditGenTymSchemaFieldDto {

    @NotNull(message = "主键ID不能为空")
    @Schema(description = "主键ID")
    private Long id;

    @NotNull(message = "类型映射方案ID不能为空")
    @Schema(description = "类型映射方案ID")
    private Long typeSchemaId;

    @NotBlank(message = "匹配源类型不能为空")
    @Size(max = 80, min = 1, message = "匹配源类型长度必须在1-80个字符之间")
    @Schema(description = "匹配源类型")
    private String source;

    @NotBlank(message = "匹配目标类型不能为空")
    @Size(max = 80, min = 1, message = "匹配目标类型长度必须在1-80个字符之间")
    @Schema(description = "匹配目标类型")
    private String target;

    @NotNull(message = "排序不能为空")
    @Range(min = 0, message = "排序不能小于0")
    @Schema(description = "排序")
    private Integer seq;

}
