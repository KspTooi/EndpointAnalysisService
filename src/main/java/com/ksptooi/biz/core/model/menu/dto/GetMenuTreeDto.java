package com.ksptooi.biz.core.model.menu.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetMenuTreeDto {

    @Schema(description = "菜单名称/描述(模糊)")
    private String name;

    @Schema(description = "菜单类型 0:目录 1:菜单 2:按钮")
    private Integer menuKind;

    @Schema(description = "权限(模糊)")
    private String permission;

}
