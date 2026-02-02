package com.ksptooi.biz.core.model.session;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "core_user_session", comment = "用户会话")
@Data
public class UserSessionPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "会话ID")
    private Long id;

    @Column(name = "user_id", nullable = false, comment = "用户ID")
    private Long userId;

    @Column(name = "company_id", nullable = true, comment = "公司ID")
    private Long companyId;

    @Column(name = "token", nullable = false, unique = true, length = 100, comment = "Token")
    private String token;

    @Column(name = "permissions", nullable = false, columnDefinition = "TEXT", comment = "用户权限JSON")
    private String permissions;

    @Column(name = "expires_at", nullable = false, comment = "过期时间")
    private LocalDateTime expiresAt;

    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "修改时间")
    private LocalDateTime updateTime;


    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
}