package com.ksptooi.biz.core.model.relayserver;

import org.hibernate.validator.constraints.Range;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RelayServerRouteRuleDto {

    @Schema(description = "路由规则ID")
    @NotNull(message = "路由规则ID不能为空")
    private Long routeRuleId;

    @Range(min = 0, max = 10000, message = "权重只能在0-10000之间")
    @Schema(description = "权重")
    @NotNull(message = "权重不能为空")
    private Integer seq;

}
