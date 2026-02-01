package com.ksptooi.biz.core.model.org.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetOrgDetailsVo {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "一级组织ID")
    private Long rootId;

    @Schema(description = "上级组织ID NULL顶级组织")
    private Long parentId;

    @Schema(description = "0:部门 1:企业")
    private Integer kind;

    @Schema(description = "组织机构名称")
    private String name;

    @Schema(description = "主管ID")
    private Long principalId;

    @Schema(description = "主管名称")
    private String principalName;

}

