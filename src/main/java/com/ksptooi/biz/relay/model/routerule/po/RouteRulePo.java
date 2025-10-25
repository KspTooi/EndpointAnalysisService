package com.ksptooi.biz.relay.model.routerule.po;

import com.ksptooi.biz.relay.model.routeserver.po.RouteServerPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "route_rule")
@Comment("路由规则")
public class RouteRulePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("路由规则ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 32)
    @Comment("路由策略名")
    private String name;

    @Column(name = "match_type", nullable = false, columnDefinition = "tinyint")
    @Comment("匹配类型 0:全部 1:IP地址")
    private Integer matchType;

    @Column(name = "match_key", length = 255)
    @Comment("匹配键")
    private String matchKey;

    @Column(name = "match_operator")
    @Comment("匹配操作 0:等于")
    private Integer matchOperator;

    @Column(name = "match_value", length = 255)
    @Comment("匹配值")
    private String matchValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_server_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @Comment("目标服务器")
    private RouteServerPo routeServer;

    @Column(name = "remark", length = 5000, columnDefinition = "text")
    @Comment("策略描述")
    private String remark;

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
