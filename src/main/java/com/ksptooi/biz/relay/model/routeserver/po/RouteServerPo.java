package com.ksptooi.biz.relay.model.routeserver.po;

import com.ksptooi.biz.relay.model.routerule.po.RouteRulePo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "relay_route_server", comment = "路由服务器")
public class RouteServerPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "路由服务器ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 32, comment = "服务器名称")
    private String name;

    @Column(name = "host", nullable = false, length = 32, comment = "服务器主机")
    private String host;

    @Column(name = "port", nullable = false, comment = "服务器端口")
    private Integer port;

    @Column(name = "remark", nullable = true, length = 5000, comment = "备注")
    private String remark;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint", comment = "服务器状态 0:禁用 1:启用")
    private Integer status;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "routeServer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RouteRulePo> routeRules;

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
