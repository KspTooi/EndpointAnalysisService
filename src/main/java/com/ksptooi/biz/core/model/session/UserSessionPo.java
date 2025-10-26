package com.ksptooi.biz.core.model.session;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;

import java.util.Date;

@Entity
@Table(name = "core_user_session")
@Data
@Comment("用户会话")
public class UserSessionPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("会话ID")
    private Long id;

    @Column(name = "user_id", nullable = false)
    @Comment("用户ID")
    private Long userId;

    @Column(name = "token", nullable = false, unique = true, length = 100)
    @Comment("Token")
    private String token;

    @Column(name = "permissions", nullable = false, columnDefinition = "TEXT")
    @Comment("用户权限JSON")
    private String permissions;

    @Column(name = "expires_at", nullable = false)
    @Comment("过期时间")
    private Date expiresAt;

    @Column(name = "create_time", nullable = false, updatable = false)
    @Comment("创建时间")
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("修改时间")
    private Date updateTime;


    @PrePersist
    public void prePersist() {
        createTime = new Date();
        updateTime = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = new Date();
    }
}