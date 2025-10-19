package com.ksptooi.biz.core.model.relayserver;

import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class EditRelayServerDto {

    @Schema(description = "中继服务器ID")
    @NotNull(message = "中继服务器ID不能为空")
    private Long id;

    @Schema(description = "中继服务器名称")
    @NotBlank(message = "中继服务器名称不能为空")
    private String name;

    @Schema(description = "中继服务器主机")
    @NotBlank(message = "中继服务器主机不能为空")
    private String host;

    @Schema(description = "中继服务器端口")
    @NotNull(message = "中继服务器端口不能为空")
    private Integer port;

    @Schema(description = "桥接目标类型")
    @NotNull(message = "桥接目标类型不能为空")
    @Range(min = 0, max = 1, message = "桥接目标类型只能为0或1")
    private Integer forwardType;

    @Schema(description = "路由规则列表(桥接目标类型为1时必填)")
    @NotNull(message = "路由规则列表不能为null")
    private List<RelayServerRouteRuleDto> routeRules;

    @Schema(description = "桥接目标URL")
    private String forwardUrl;

    @Schema(description = "自动运行 0:否 1:是")
    @NotNull(message = "自动运行不能为空")
    @Range(min = 0, max = 1, message = "自动运行只能为0或1")
    private Integer autoStart;

    @Schema(description = "覆盖桥接目标的重定向 0:否 1:是")
    @NotNull(message = "覆盖桥接目标的重定向不能为空")
    @Range(min = 0, max = 1, message = "覆盖桥接目标的重定向只能为0或1")
    private Integer overrideRedirect;

    @Schema(description = "覆盖桥接目标的重定向URL")
    private String overrideRedirectUrl;

    @Schema(description = "请求ID策略 0:随机生成 1:从请求头获取")
    @NotNull(message = "请求ID策略不能为空")
    @Range(min = 0, max = 1, message = "请求ID策略只能为0或1")
    private Integer requestIdStrategy;

    @Schema(description = "请求ID头名称")
    private String requestIdHeaderName;

    @Schema(description = "业务错误策略 0:由HTTP状态码决定 1:由业务错误码决定")
    @NotNull(message = "业务错误策略不能为空")
    @Range(min = 0, max = 1, message = "业务错误策略只能为0或1")
    private Integer bizErrorStrategy;

    @Schema(description = "业务错误码字段(JSONPath)")
    private String bizErrorCodeField;

    @Schema(description = "业务错误码值(正确时返回的值)")
    private String bizSuccessCodeValue;


    /**
     * 验证参数
     * @return 错误信息 当参数合法时返回null
     */
    public String validate() {

        //桥接目标类型为1时，路由规则列表不能为空
        if(forwardType == 1 && routeRules.isEmpty()) {
            
            //不允许传递桥接目标URL
            if(StringUtils.isNotBlank(forwardUrl)) {
                return "当桥接目标类型为路由时，不允许传递桥接目标URL";
            }

            return "当桥接目标类型为路由时，路由规则列表不能为空";
        }

        //桥接目标类型为0时，校验桥接目标
        if(forwardType == 0 && StringUtils.isBlank(forwardUrl)) {

            //不允许传递路由规则列表
            if(!routeRules.isEmpty()) {
                return "当桥接目标类型为直接时，不允许传递路由规则列表";
            }

            //桥接目标URL必须为有效URL http:// 或 https:// 且支持域名
            if(StringUtils.isBlank(forwardUrl)) {
                return "桥接目标URL不能为空";
            }

            if(!forwardUrl.matches("^https?://[A-Za-z0-9.-]+(?::\\d+)?(?:/\\S*)?$")) {
                return "桥接目标URL必须为有效URL";
            }

            return "当桥接目标类型为直接时，桥接目标URL不能为空";
        }

        //主机名必须为有效IP地址
        if(!host.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
            return "主机名必须为有效IP地址";
        }
        //端口必须为1-65535之间的整数
        if(port < 1 || port > 65535) {
            return "端口必须为1-65535之间的整数";
        }

        //当覆盖桥接目标的重定向为1时，覆盖桥接目标的重定向URL不能为空
        if(overrideRedirect == 1 && StringUtils.isBlank(overrideRedirectUrl)) {
            return "覆盖桥接目标的重定向URL不能为空";
        }

        //当覆盖桥接目标的重定向为1时，覆盖桥接目标的重定向URL必须为有效URL https:// or http://
        if(overrideRedirect == 1 && !overrideRedirectUrl.matches("^https?://\\S+$") && !overrideRedirectUrl.matches("^http://\\S+$")) {
            return "覆盖桥接目标的重定向URL必须为有效URL";
        }

        //当请求ID策略为1时，请求ID头名称不能为空
        if(requestIdStrategy == 1 && StringUtils.isBlank(requestIdHeaderName)) {
            return "请求ID头名称不能为空";
        }

        //当业务错误策略为1时，业务错误码字段不能为空
        if(bizErrorStrategy == 1 && StringUtils.isBlank(bizErrorCodeField)) {
            return "业务错误码字段不能为空";
        }

        //当业务错误策略为1时，业务错误码值不能为空
        if(bizErrorStrategy == 1 && StringUtils.isBlank(bizSuccessCodeValue)) {
            return "业务错误码值不能为空";
        }

        return null;
    }

}
