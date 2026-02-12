package com.ksptooi.biz.core.model.endpoint;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * 动态端点权限配置规则
 */
@Getter@Setter
public class DynamicEndpointAuthorizationRule {

    @Schema(description = "路径模式")
    private String pathPattern;

    @Schema(description = "所需权限码 多个;分隔 无需权限为*")
    private String permissionCodes;

}
