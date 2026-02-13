package com.ksptooi.biz.auth.model.profile.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetCurrentUserProfilePermissionVo {

    @Schema(description = "权限代码")
    private String code;

    @Schema(description = "权限名称")
    private String name;

}
