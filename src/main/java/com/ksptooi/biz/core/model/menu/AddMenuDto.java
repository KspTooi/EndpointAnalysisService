package com.ksptooi.biz.core.model.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMenuDto {

    @NotNull(message = "父级ID不能为空")
    @Schema(description = "父级ID -1:根节点")
    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "菜单描述")
    private String description;

    @Schema(description = "资源类型 0:菜单 1:接口")
    private Integer kind;

    @NotNull(message = "菜单类型不能为空")
    @Schema(description = "菜单类型 0:目录 1:菜单 2:按钮")
    private Integer menuKind;

    @Schema(description = "菜单路径(目录不能填写)")
    private String menuPath;

    @Schema(description = "菜单查询参数(目录不能填写)")
    private String menuQueryParam;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "所需权限(目录不能填写)")
    private String permission;

    @Min(value = 0, message = "排序不能为负数")
    @Schema(description = "排序")
    private Integer seq;

}

