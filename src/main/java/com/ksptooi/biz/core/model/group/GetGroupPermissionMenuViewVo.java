package com.ksptooi.biz.core.model.group;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.ksptooi.commons.dataprocess.Str;

@Getter@Setter
public class GetGroupPermissionMenuViewVo {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父级ID -1:根节点")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "菜单图标")
    private String menuIcon;

    @Schema(description = "菜单类型 0:目录 1:菜单 2:按钮")
    private Integer menuKind;

    @Schema(description = "菜单路径(目录不能填写)")
    private String menuPath;

    @Schema(description = "按钮ID(menuKind = 2时必填)")
    private String menuBtnId;

    @Schema(description = "所需权限(目录不能填写)")
    private String permission;

    @Schema(description = "是否缺失权限节点 0:否 1:是 2:部分缺失")
    private Integer missingPermission;

    @Schema(description = "当前用户是否有权限 0:否 1:是 2:部分授权")
    private Integer hasPermission;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "子菜单")
    private List<GetGroupPermissionMenuViewVo> children;

    /**
     * 获取权限列表
     * @return 权限列表
     */
    public Set<String> getPermissions() {

        if (StringUtils.isBlank(permission) || Str.in(permission, "*")) {
            return Collections.emptySet();
        }

        //有；代表有多个权限，需要分割
        if (permission.contains(";")) {
            return new HashSet<>(Str.safeSplit(permission, ";"));
        }

        //只有一个权限，直接返回
        return Collections.singleton(permission);
    }
}
