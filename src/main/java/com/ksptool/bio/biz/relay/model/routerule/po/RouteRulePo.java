package com.ksptool.bio.biz.relay.model.routerule.po;

import com.ksptool.bio.biz.relay.model.relayserverroute.po.RelayServerRoutePo;
import com.ksptool.bio.biz.relay.model.routeserver.po.RouteServerPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "relay_route_rule", comment = "路由规则")
public class RouteRulePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "路由规则ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 32, comment = "路由策略名")
    private String name;

    @Column(name = "match_type", nullable = false, columnDefinition = "tinyint", comment = "匹配类型 0:全部 1:IP地址")
    private Integer matchType;

    @Column(name = "match_key", length = 255, comment = "匹配键")
    private String matchKey;

    @Column(name = "match_operator", comment = "匹配操作 0:等于")
    private Integer matchOperator;

    @Column(name = "match_value", length = 255, comment = "匹配值")
    private String matchValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_server_id", nullable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), comment = "目标服务器")
    private RouteServerPo routeServer;

    @Column(name = "remark", length = 5000, columnDefinition = "text", comment = "策略描述")
    private String remark;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "routeRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RelayServerRoutePo> relayServerRoutes;


    @PrePersist
    private void onCreate() {

        LocalDateTime now = LocalDateTime.now();
        if (this.createTime == null) {
            this.createTime = now;
        }
        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }

    }

    @PreUpdate
    private void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
