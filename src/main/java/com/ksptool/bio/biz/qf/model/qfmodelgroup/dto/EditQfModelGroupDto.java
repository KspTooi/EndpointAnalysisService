package com.ksptool.bio.biz.qf.model.qfmodelgroup.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditQfModelGroupDto {

    @Schema(description = "主键ID")
    @NotNull(message = "主键ID不能为空")
    private Long id;

    @Schema(description = "组名称")
    @NotBlank(message = "组名称不能为空")
    @Size(max = 80, message = "组名称长度不能超过80个字符")
    private String name;

    @Schema(description = "组编码")
    @NotBlank(message = "组编码不能为空")
    @Size(max = 32, message = "组编码长度不能超过32个字符")
    private String code;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "排序")
    @NotNull(message = "排序不能为空")
    private Integer seq;

}
