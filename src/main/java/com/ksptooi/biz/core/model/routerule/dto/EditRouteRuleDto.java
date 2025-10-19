package com.ksptooi.biz.core.model.routerule.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditRouteRuleDto {

    @Schema(description = "路由规则ID")
    private Long id;

    @Schema(description = "路由策略名")
    private String name;

    @Schema(description = "匹配类型 0:全部 1:IP地址")
    private Integer matchType;

    @Schema(description = "匹配键")
    private String matchKey;

    @Schema(description = "匹配操作 0:等于")
    private Integer matchOperator;

    @Schema(description = "匹配值")
    private String matchValue;

    @Schema(description = "目标服务器ID")
    private Long routeServerId;

    @Schema(description = "权重")
    private Integer seq;

    @Schema(description = "策略描述")
    private String remark;

}

