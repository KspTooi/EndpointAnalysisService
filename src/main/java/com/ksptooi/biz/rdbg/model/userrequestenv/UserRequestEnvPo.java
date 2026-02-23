package com.ksptooi.biz.rdbg.model.userrequestenv;

import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptooi.biz.rdbg.model.userrequestenvstorage.UserRequestEnvStoragePo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "rdbg_user_env")
public class UserRequestEnvPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "环境ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "用户ID")
    private UserPo user;

    @Column(name = "name", nullable = false, length = 32, comment = "环境名")
    private String name;

    @Column(name = "remark", columnDefinition = "text", length = 5000, comment = "描述")
    private String remark;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "env", cascade = CascadeType.ALL, orphanRemoval = true)
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
