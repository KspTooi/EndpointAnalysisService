package com.ksptooi.biz.core.model.relayserver;

import com.ksptooi.biz.core.model.relayserverroute.po.RelayServerRoutePo;
import com.ksptooi.biz.core.model.request.RequestPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "relay_server")
@Getter
@Setter
@Comment("中继服务器")
public class RelayServerPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("中继服务器ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 128)
    @Comment("中继服务器名称")
    private String name;

    @Column(name = "host", nullable = false, length = 128)
    @Comment("中继服务器主机")
    private String host;

    @Column(name = "port", nullable = false)
    @Comment("中继服务器端口")
    private Integer port;

    @Column(name = "forward_type", nullable = false, columnDefinition = "tinyint")
    @Comment("桥接目标类型 0:直接 1:路由")
    private Integer forwardType;

    @Column(name = "forward_url", length = 320)
    @Comment("桥接目标URL")
    private String forwardUrl;

    @Column(name = "auto_start", nullable = false, columnDefinition = "tinyint")
    @Comment("自动运行 0:否 1:是")
    private Integer autoStart;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint")
    @Comment("中继服务器状态 0:已禁用 1:未启动 2:运行中 3:启动失败")
    private Integer status;

    @Column(name = "error_message", nullable = true, columnDefinition = "longtext")
    @Comment("启动失败原因")
    private String errorMessage;

    @Column(name = "override_redirect", nullable = false, columnDefinition = "tinyint")
    @Comment("覆盖桥接目标的重定向 0:否 1:是")
    private Integer overrideRedirect;

    @Column(name = "override_redirect_url", nullable = true, length = 320)
    @Comment("覆盖桥接目标的重定向URL")
    private String overrideRedirectUrl;

    @Column(name = "request_id_strategy", nullable = false, columnDefinition = "tinyint")
    @Comment("请求ID策略 0:随机生成 1:从请求头获取")
    private Integer requestIdStrategy;

    @Column(name = "request_id_header_name", nullable = true, length = 128)
    @Comment("请求ID头名称")
    private String requestIdHeaderName;

    @Column(name = "biz_error_strategy", nullable = false, columnDefinition = "tinyint")
    @Comment("业务错误策略 0:由HTTP状态码决定 1:由业务错误码决定")
    private Integer bizErrorStrategy;

    @Column(name = "biz_error_code_field", nullable = true, length = 128)
    @Comment("业务错误码字段(JSONPath)")
    private String bizErrorCodeField;

    @Column(name = "biz_success_code_value", nullable = true, length = 128)
    @Comment("业务成功码值(正确时返回的值)")
    private String bizSuccessCodeValue;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "relayServer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("请求记录")
    private List<RequestPo> requestList;

    @OrderBy("seq ASC")
    @BatchSize(size = 20)
    @OneToMany(mappedBy = "relayServer", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("路由规则")
    private List<RelayServerRoutePo> routeRules;


    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 添加路由规则
     * @param routeRule 路由规则
     */
    public void addRouteRule(RelayServerRoutePo routeRule) {
        if(this.routeRules == null){
            this.routeRules = new ArrayList<>();
        }
        this.routeRules.add(routeRule);
        routeRule.setRelayServer(this);
    }

    /**
     * 清空路由规则
     */
    public void clearRouteRules() {
        if(this.routeRules == null){
            return;
        }
        for(RelayServerRoutePo item : this.routeRules) {
            item.setRelayServer(null);
            item.setRouteRule(null);
        }
        this.routeRules.clear();
    }

}
