package com.ksptooi.biz.relay.model.relayserver;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class GetRelayServerDetailsVo {

    @Schema(description = "中继服务器ID")
    private Long id;

    @Schema(description = "中继服务器名称")
    private String name;

    @Schema(description = "中继服务器主机")
    private String host;

    @Schema(description = "中继服务器端口")
    private Integer port;

    @Schema(description = "桥接目标类型 0:直接 1:路由")
    private Integer forwardType;

    @Schema(description = "路由规则列表(桥接目标类型为1时必填)")
    private List<RelayServerRouteRuleVo> routeRules;

    @Schema(description = "桥接目标URL")
    private String forwardUrl;

    @Schema(description = "自动运行 0:否 1:是")
    private Integer autoStart;

    @Schema(description = "中继服务器状态 0:已禁用 1:未启动 2:运行中 3:启动失败")
    private Integer status;

    @Schema(description = "启动失败原因")
    private String errorMessage;

    @Schema(description = "覆盖桥接目标的重定向 0:否 1:是")
    private Integer overrideRedirect;

    @Schema(description = "覆盖桥接目标的重定向URL")
    private String overrideRedirectUrl;

    @Schema(description = "请求ID策略 0:随机生成 1:从响应头获取")
    private Integer requestIdStrategy;

    @Schema(description = "请求ID在响应头中的字段名称")
    private String requestIdHeaderName;

    @Schema(description = "业务错误策略 0:由HTTP状态码决定 1:由业务错误码决定")
    private Integer bizErrorStrategy;

    @Schema(description = "业务错误码字段(JSONPath)")
    private String bizErrorCodeField;

    @Schema(description = "业务错误码值(正确时返回的值)")
    private String bizSuccessCodeValue;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
