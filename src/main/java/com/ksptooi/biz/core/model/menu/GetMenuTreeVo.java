package com.ksptooi.biz.core.model.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GetMenuTreeVo {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父级ID -1:根节点")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "资源类型 0:菜单 1:接口")
    private Integer kind;

    @Schema(description = "菜单类型 0:目录 1:菜单 2:按钮")
    private Integer menuKind;

    @Schema(description = "菜单路径(目录不能填写)")
    private String menuPath;

    @Schema(description = "所需权限(目录不能填写)")
    private String permission;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "子菜单")
    private List<GetMenuTreeVo> children;

}
