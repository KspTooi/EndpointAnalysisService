package com.ksptooi.biz.relay.model.relayserverroute.po;

import com.ksptooi.biz.relay.model.relayserver.RelayServerPo;
import com.ksptooi.biz.relay.model.routerule.po.RouteRulePo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "relay_server_route")
public class RelayServerRoutePo {

    @Column(name = "id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("路由表ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relay_server_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @Comment("中继服务器ID")
    private RelayServerPo relayServer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_rule_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @Comment("路由策略ID")
    private RouteRulePo routeRule;

    @Column(name = "seq", nullable = false)
    @Comment("权重")
    private Integer seq;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

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
