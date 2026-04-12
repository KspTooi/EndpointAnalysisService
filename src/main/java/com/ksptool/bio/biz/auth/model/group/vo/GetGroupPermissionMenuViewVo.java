package com.ksptool.bio.biz.auth.model.group.vo;

import com.ksptool.bio.commons.dataprocess.Str;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class GetGroupPermissionMenuViewVo {

    @Schema(description = "菜单ID")
    private Long id;

    @Schema(description = "父级ID null:根节点")
    private Long parentId;

    @Schema(description = "菜单名称")
    private String name;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "菜单类型 0:目录 1:菜单 2:按钮")
    private Integer kind;

    @Schema(description = "菜单路径")
    private String path;

    @Schema(description = "所需权限码")
    private String permissionCode;

    @Schema(description = "是否缺失权限节点 0:否 1:完全缺失 2:部分缺失")
    private Integer missingPermission;

    @Schema(description = "当前组是否有权限 0:否 1:是 2:部分授权")
    private Integer hasPermission;

    @Schema(description = "排序")
    private Integer seq;

    @Schema(description = "子菜单")
    private List<GetGroupPermissionMenuViewVo> children;

    /**
     * 获取权限列表
     *
     * @return 权限列表
     */
    public Set<String> getPermissions() {

        if (StringUtils.isBlank(permissionCode)) {
            return Collections.emptySet();
        }

        if (permissionCode.contains(";")) {
            return new HashSet<>(Str.safeSplit(permissionCode, ";"));
        }

        return Collections.singleton(permissionCode);
    }
}
