package com.ksptooi.biz.core.model.group;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class GetGroupPermissionMenuViewDto {

    @Schema(description = "组ID")
    @NotNull(message = "组ID不能为空")
    private Long groupId;

    @Schema(description = "模糊匹配 菜单名称、菜单路径")
    private String keyword;

    @Schema(description = "是否已授权 0:否 1:是 2:部分授权")
    private Integer hasPermission;

}
