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
    private String remark;

    @Schema(description = "系统内置组 0:否 1:是")
    private Integer isSystem;

    @Schema(description = "组状态：0-禁用，1-启用")
    private Integer status;

    @Schema(description = "排序号")
    private Integer seq;

    @Schema(description = "数据权限 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门")
    private Integer rowScope;

    @Schema(description = "部门ID列表")
    private List<Long> deptIds;

    @Schema(description = "权限节点列表")
    private List<GroupPermissionDefinitionVo> permissions;
}
