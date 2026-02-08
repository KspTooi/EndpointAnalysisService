package com.ksptooi.biz.core.model.registry.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Getter
@Setter
public class EditRegistryDto {

    @Schema(description = "ID")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    @NotNull(message = "数据类型不能为空")
    @Range(min = 0, max = 3, message = "数据类型只能在0-3之间")
    private Integer nvalueKind;

    @Schema(description = "值")
    private String nvalue;

    @Schema(description = "标签")
    @Length(max = 512, message = "标签长度不能超过512个字符")
    private String label;

    @Schema(description = "状态 0:正常 1:停用")
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 1, message = "状态只能是0或1")
    private Integer status;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer seq;

    @Schema(description = "说明")
    private String remark;

    @Schema(description = "元数据JSON")
    @NotBlank(message = "元数据不能为空")
    private String metadata;
}
