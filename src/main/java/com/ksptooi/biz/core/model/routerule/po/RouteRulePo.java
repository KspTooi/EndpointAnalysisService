package com.ksptooi.biz.core.model.routerule.po;

import com.ksptooi.biz.core.model.routegroup.po.RouteGroupPo;
import com.ksptooi.biz.core.model.routeserver.po.RouteServerPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "route_rule")
@Getter
@Setter
@Comment("路由规则")
public class RouteRulePo {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("路由规则ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false, foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    @Comment("所属组ID")
    private RouteGroupPo group;

    @Column(name = "match_value", nullable = true, length = 32)
    @Comment("匹配来源IP 为null时匹配所有IP")
    private String matchValue;

    @ManyToOne
    @JoinColumn(name = "route_server_id", nullable = false)
    @Comment("所属服务器ID")
    private RouteServerPo routeServer;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = this.createTime;
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }
}
