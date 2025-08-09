package com.ksptooi.biz.core.model.relayserver;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "relay_server")
@Getter @Setter
public class RelayServerPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("中继服务器ID")
    private Long id;

    @Column(name = "name", nullable = false,length = 128)
    @Comment("中继服务器名称")
    private String name;

    @Column(name = "host", nullable = false,length = 128)
    @Comment("中继服务器主机")
    private String host;

    @Column(name = "port", nullable = false)
    @Comment("中继服务器端口")
    private Integer port;

    @Column(name = "forward_url", nullable = false,length = 320)
    @Comment("桥接目标URL")
    private String forwardUrl;

    @Column(name = "auto_start", nullable = false,columnDefinition = "tinyint")
    @Comment("自动运行 0:否 1:是")
    private Integer autoStart;

    @Column(name = "status", nullable = false,columnDefinition = "tinyint")
    @Comment("中继服务器状态 0:已禁用 1:未启动 2:运行中 3:启动失败")
    private Integer status;

    @Column(name = "error_message", nullable = true,columnDefinition = "longtext")
    @Comment("启动失败原因")
    private String errorMessage;

    @Column(name = "override_redirect", nullable = false,columnDefinition = "tinyint default 0")
    @Comment("覆盖桥接目标的重定向 0:否 1:是")
    private Integer overrideRedirect;

    @Column(name = "override_redirect_url", nullable = true,length = 320)
    @Comment("覆盖桥接目标的重定向URL")
    private String overrideRedirectUrl;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;
    

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

}
