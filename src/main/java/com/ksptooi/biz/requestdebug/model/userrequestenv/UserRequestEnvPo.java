package com.ksptooi.biz.requestdebug.model.userrequestenv;

import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.biz.requestdebug.model.userrequestenvstorage.UserRequestEnvStoragePo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "user_request_env")
public class UserRequestEnvPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("环境ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("用户ID")
    private UserPo user;

    @Column(name = "name", nullable = false, length = 32)
    @Comment("环境名")
    private String name;

    @Column(name = "remark", columnDefinition = "text", length = 5000)
    @Comment("描述")
    private String remark;

    @Column(name = "active", columnDefinition = "tinyint", nullable = false)
    @Comment("激活状态 0:启用 1:禁用")
    private Integer active;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "env", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("环境共享存储")
    private List<UserRequestEnvStoragePo> storages;

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
