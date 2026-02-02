package com.ksptooi.biz.core.model.org.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditOrgDto {

    @NotNull(message = "主键id不能为空")
    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "上级组织ID NULL顶级组织")
    private Long parentId;

    /* @Range(min = 0, max = 1, message = "组织机构类型必须在0和1之间")
    @NotNull(message = "组织机构类型不能为空")
    @Schema(description = "0:部门 1:企业")
    private Integer kind; */

    @NotNull(message = "组织机构名称不能为空")
    @Size(max = 128, message = "组织机构名称长度不能超过128个字符")
    @Schema(description = "组织机构名称")
    private String name;

    @Schema(description = "主管ID")
    private Long principalId;

    @NotNull(message = "排序不能为空")
    @Schema(description = "排序")
    private Integer seq;

}

