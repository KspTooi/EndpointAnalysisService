package com.ksptooi.biz.core.model.relayserver;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class AddRelayServerDto {

    //中继服务器名称
    @NotBlank(message = "中继服务器名称不能为空")
    private String name;

    //中继服务器主机
    @NotBlank(message = "中继服务器主机不能为空")
    private String host;

    //中继服务器端口
    @NotNull(message = "中继服务器端口不能为空")
    private Integer port;

    //桥接目标URL
    @NotBlank(message = "桥接目标URL不能为空")
    private String forwardUrl;

    //自动运行 0:否 1:是
    @NotNull(message = "自动运行不能为空")
    @Range(min = 0, max = 1, message = "自动运行只能为0或1")
    private Integer autoStart;

    //覆盖桥接目标的重定向 0:否 1:是
    @NotNull(message = "覆盖桥接目标的重定向不能为空")
    @Range(min = 0, max = 1, message = "覆盖桥接目标的重定向只能为0或1")
    private Integer overrideRedirect;

    //覆盖桥接目标的重定向URL
    private String overrideRedirectUrl;

    //请求ID策略 0:随机生成 1:从请求头获取
    @NotNull(message = "请求ID策略不能为空")
    @Range(min = 0, max = 1, message = "请求ID策略只能为0或1")
    private Integer requestIdStrategy;

    //请求ID头名称
    private String requestIdHeaderName;

    //业务错误策略 0:由HTTP状态码决定 1:由业务错误码决定
    @NotNull(message = "业务错误策略不能为空")
    @Range(min = 0, max = 1, message = "业务错误策略只能为0或1")
    private Integer bizErrorStrategy;

    //业务错误码字段(JSONPath)
    private String bizErrorCodeField;

    //业务错误码值(正确时返回的值)
    private String bizSuccessCodeValue;

    //覆盖桥接目标的重定向URL
    /**
     * 验证参数
     * @return 错误信息 当参数合法时返回null
     */
    public String validate() {

        //主机名必须为有效IP地址
        if(!host.matches("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$")) {
            return "主机名必须为有效IP地址";
        }
        //端口必须为1-65535之间的整数
        if(port < 1 || port > 65535) {
            return "端口必须为1-65535之间的整数";
        }

        //桥接目标URL必须为有效URL http:// 或 https:// 且支持域名
        if(StringUtils.isBlank(forwardUrl)) {
            return "桥接目标URL不能为空";
        }
        if(!forwardUrl.matches("^https?://[A-Za-z0-9.-]+(?::\\d+)?(?:/\\S*)?$")) {
            return "桥接目标URL必须为有效URL";
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
