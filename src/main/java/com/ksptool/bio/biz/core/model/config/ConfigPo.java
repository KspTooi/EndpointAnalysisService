package com.ksptool.bio.biz.core.model.config;

import com.ksptool.bio.biz.core.model.user.UserPo;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "core_config", comment = "配置表", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "config_key"}, name = "uk_config")
})
public class ConfigPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "主键ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "用户ID 为null表示全局配置")
    private UserPo user;

    @Column(name = "config_key", nullable = false, length = 100, comment = "配置键")
    private String configKey;

    @Column(name = "config_value", length = 500, comment = "配置值")
    private String configValue;

    @Column(name = "description", length = 200, comment = "配置描述")
    private String description;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
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