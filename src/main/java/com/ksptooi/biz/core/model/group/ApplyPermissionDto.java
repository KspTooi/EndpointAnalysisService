package com.ksptooi.biz.core.model.group;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ApplyPermissionDto {

    @Schema(description = "组ID")
    @NotNull(message = "组ID不能为空")
    private Long groupId;

    @Schema(description = "权限代码列表")
    @NotNull(message = "权限代码列表不能为空")
    private List<String> permissionCodes;

}
