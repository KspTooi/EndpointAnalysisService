package com.ksptooi.biz.core.model.routeserver.po;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "route_server")
@Comment("路由服务器")
public class RouteServerPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("路由服务器ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 32)
    @Comment("服务器名称")
    private String name;

    @Column(name = "host", nullable = false, length = 32)
    @Comment("服务器主机")
    private String host;

    @Column(name = "port", nullable = false)
    @Comment("服务器端口")
    private Integer port;

    @Column(name = "remark", nullable = true, length = 5000)
    @Comment("备注")
    private String remark;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint")
    @Comment("服务器状态 0:禁用 1:启用")
    private Integer status;

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
