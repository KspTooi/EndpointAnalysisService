package com.ksptooi.biz.core.model.relayserverroute.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class GetRelayServerRouteDetailsVo {

    @Schema(description = "路由表ID")
    private Long id;

    @Schema(description = "中继服务器ID")
    private Long relayServerId;

    @Schema(description = "路由策略ID")
    private Long routeRuleId;

    @Schema(description = "权重")
    private Integer seq;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "更新时间")
    private Date updateTime;

}

