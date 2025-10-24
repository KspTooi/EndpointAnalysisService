package com.ksptooi.biz.core.model.menu;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditMenuDto {

    @NotNull(message = "菜单ID不能为空")
    @Schema(description = "菜单ID")
    private Long id;

    @NotNull(message = "父级ID不能为空")
    @Schema(description = "父级ID -1:根节点")
    private Long parentId;

    @NotBlank(message = "菜单名称不能为空")
    @Length(min = 2, max = 128, message = "菜单名称长度必须在2-32个字符之间")
    @Schema(description = "菜单名称")
    private String name;

    @Length(max = 200, message = "菜单描述长度不能超过200个字符")
    @Schema(description = "菜单描述")
    private String description;

    @NotNull(message = "菜单类型不能为空")
    @Range(min = 0, max = 2, message = "菜单类型只能在0-2之间")
    @Schema(description = "菜单类型 0:目录 1:菜单 2:按钮")
    private Integer menuKind;

    @Length(max = 256, message = "菜单路径长度不能超过256个字符")
    @Schema(description = "菜单路径(目录不能填写)")
    private String menuPath;

    @Length(max = 256, message = "菜单查询参数长度不能超过512个字符")
    @Schema(description = "菜单查询参数(目录不能填写)")
    private String menuQueryParam;

    @Length(max = 64, message = "菜单图标长度不能超过64个字符")
    @Schema(description = "菜单图标")
    private String menuIcon;

    @Length(max = 320, message = "所需权限长度不能超过320个字符")
    @Schema(description = "所需权限(目录不能填写)")
    private String permission;

    @Range(min = 0,max = 655350, message = "排序只能在0-655350之间")
    @Schema(description = "排序")
    private Integer seq;

}

