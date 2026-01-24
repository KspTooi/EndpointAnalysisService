package com.ksptooi.biz.core.model.dept.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetDeptListVo {

    @Schema(description = "部门ID")
    private Long id;

    @Schema(description = "父级部门 NULL为顶级")
    private Long parentId;

    @Schema(description = "部门名")
    private String name;

    @Schema(description = "负责人ID")
    private Long principalId;

    @Schema(description = "负责人名称")
    private String principalName;

    @Schema(description = "部门状态 0:正常 1:禁用")
    private Integer status;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creatorId;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updaterId;

}

