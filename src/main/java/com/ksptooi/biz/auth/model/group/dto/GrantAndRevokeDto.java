package com.ksptooi.biz.auth.model.group.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class GrantAndRevokeDto {

    @Schema(description = "组ID")
    @NotNull(message = "组ID不能为空")
    private Long groupId;

    @Schema(description = "权限代码列表")
    @NotNull(message = "权限代码列表不能为空")
    private Set<String> permissionCodes;

    @Schema(description = "类型 0:授权 1:取消授权")
    @NotNull(message = "类型不能为空")
    @Min(value = 0, message = "类型值不正确")
    @Max(value = 1, message = "类型值不正确")
    private Integer type;


}
