package com.ksptooi.biz.core.model.relayserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelayServerRouteRuleVo {

    @Schema(description = "路由规则ID")
    private Long routeRuleId;

    @Schema(description = "路由规则名称")
    private String routeRuleName;

    @Schema(description = "权重")
    private Integer seq;

}
