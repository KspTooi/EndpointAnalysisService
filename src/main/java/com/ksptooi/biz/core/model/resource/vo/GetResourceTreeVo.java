package com.ksptooi.biz.core.model.resource.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetResourceTreeVo {

    @Schema(description="主键id")
    private Long id;

    @Schema(description="父级ID")
    private Long parentId;

    @Schema(description="资源名")
    private String name;

    @Schema(description="资源描述")
    private String description;

    @Schema(description="资源类型 0:菜单 1:接口")
    private Integer kind;

    @Schema(description="菜单类型 0:目录 1:菜单 1:按钮")
    private Integer menuKind;

    @Schema(description="菜单路径")
    private String menuPath;

    @Schema(description="接口路径")
    private String path;

    @Schema(description="所需权限")
    private String permission;

    @Schema(description="排序")
    private Integer seq;

}
