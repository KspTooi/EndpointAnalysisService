package com.ksptool.bio.biz.qf.model.qfmodelgroup.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddQfModelGroupDto {

    @NotNull(message = "组名称不能为空")
    @Length(max = 80, message = "组名称长度不能超过80个字符")
    @Schema(description = "组名称")
    private String name;

    @NotNull(message = "组编码不能为空")
    @Length(max = 32, message = "组编码长度不能超过32个字符")
    @Schema(description = "组编码")
    private String code;

    @Length(max = 500, message = "备注长度不能超过500个字符")
    @Schema(description = "备注")
    private String remark;

    @NotNull(message = "排序不能为空")
    @Schema(description = "排序")
    @Min(value = 0, message = "排序必须大于等于0")
    private Integer seq;

}
