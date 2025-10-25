package com.ksptooi.biz.requestdebug.model.userrequestenvstorage;

import com.ksptooi.biz.requestdebug.model.userrequestenv.UserRequestEnvPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_request_env_storage")
public class UserRequestEnvStoragePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("共享存储ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "env_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("环境ID")
    private UserRequestEnvPo env;

    @Column(name = "name", nullable = false, length = 32)
    @Comment("变量名")
    private String name;

    @Column(name = "init_value", columnDefinition = "text", length = 5000)
    @Comment("初始值")
    private String initValue;

    @Column(name = "value", columnDefinition = "text", length = 5000)
    @Comment("当前值")
    private String value;

    @Column(name = "status", columnDefinition = "tinyint", nullable = false)
    @Comment("状态 0:启用 1:禁用")
    private Integer status;

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
