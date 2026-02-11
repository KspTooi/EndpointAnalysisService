package com.ksptooi.biz.auth.model.group.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetGroupDetailsVo {

    @Schema(description = "组ID")
    private Long id;

    @Schema(description = "组标识")
    private String code;

    @Schema(description = "组名称")
    private String name;

    @Schema(description = "组描述")
    private String description;

    @Schema(description = "是否系统内置组")
    private Boolean isSystem;

    @Schema(description = "组状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "排序号")
    private Integer sortOrder;

    @Schema(description = "权限节点列表")
    private List<GroupPermissionDefinitionVo> permissions;
}
