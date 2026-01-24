package com.ksptooi.biz.core.model.dept.dto;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddDeptDto {

    @Schema(description = "父级部门 NULL为顶级")
    private Long parentId;

    @NotBlank(message = "部门名不能为空")
    @Size(min = 1, max = 32, message = "部门名长度必须在1-32个字符之间")
    @Schema(description = "部门名")
    private String name;

    @Schema(description = "负责人ID")
    private Long principalId;

    @NotNull(message = "部门状态不能为空")
    @Range(min = 0, max = 1, message = "部门状态只能在0-1之间")
    @Schema(description = "部门状态 0:正常 1:禁用")
    private Integer status;

    @NotNull(message = "排序不能为空")
    @Range(min = 0, max = 655350, message = "排序只能在0-655350之间")
    @Schema(description = "排序")
    private Integer seq;

}

