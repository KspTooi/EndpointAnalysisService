package com.ksptooi.biz.audit.modal.auditlogin;

import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "audit_login")
public class AuditLoginPo {

    @Column(name = "id", comment = "登录日志主键")
    @Id
    private Long id;

    @Column(name = "user_id", comment = "用户ID")
    private Long userId;

    @Column(name = "username", comment = "用户账号")
    private String username;

    @Column(name = "login_kind", comment = "登录方式 0:用户名密码")
    private Integer loginKind;

    @Column(name = "ip_addr", comment = "登录 IP")
    private String ipAddr;

    @Column(name = "location", comment = "IP 归属地")
    private String location;

    @Column(name = "browser", comment = "浏览器/客户端指纹")
    private String browser;

    @Column(name = "os", comment = "操作系统")
    private String os;

    @Column(name = "status", comment = "状态: 0:成功 1:失败")
    private String status;

    @Column(name = "message", comment = "提示消息")
    private String message;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;


    @PrePersist
    private void onCreate() {

        if (this.id == null) {
            this.id = IdWorker.nextId();
        }


        LocalDateTime now = LocalDateTime.now();

        if (this.createTime == null) {
            this.createTime = now;
        }


    }

    @PreUpdate
    private void onUpdate() {


    }
}
