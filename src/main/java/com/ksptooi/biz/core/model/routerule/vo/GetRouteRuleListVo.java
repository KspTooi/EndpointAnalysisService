package com.ksptooi.biz.core.model.routerule.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GetRouteRuleListVo {

    @Schema(description = "路由规则ID")
    private Long id;

    @Schema(description = "路由策略名")
    private String name;

    @Schema(description = "目标服务器名称")
    private String routeServerName;

    @Schema(description = "匹配类型 0:全部 1:IP地址")
    private Integer matchType;

    @Schema(description = "匹配键")
    private String matchKey;

    @Schema(description = "匹配操作 0:等于")
    private Integer matchOperator;

    @Schema(description = "匹配值")
    private String matchValue;
    
    @Schema(description = "策略描述")
    private String remark;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}

