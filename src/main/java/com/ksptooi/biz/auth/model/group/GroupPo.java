package com.ksptooi.biz.auth.model.group;

import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 用户组实体类
 * 用于管理系统中的用户组信息，对用户进行分组并分配权限
 */

@Entity
@Table(name = "auth_group")
@Getter
@Setter
public class GroupPo {

    @Id
    @Column(name = "id", nullable = false, comment = "组ID")
    private Long id;

    @Column(name = "code", length = 32, nullable = false, comment = "组标识，如：admin、developer等")
    private String code;

    @Column(name = "name", length = 32, nullable = false, comment = "组名称，如：管理员组、开发者组等")
    private String name;

    @Column(name = "remark", columnDefinition = "TEXT", comment = "组描述")
    private String remark;

    @Column(name = "status", columnDefinition = "TINYINT", nullable = false, comment = "组状态:0:禁用，1:启用")
    private Integer status;

    @Column(name = "seq", nullable = false, comment = "排序号")
    private Integer seq;

    @Column(name = "is_system", columnDefinition = "TINYINT", nullable = false, comment = "系统内置组 0:否 1:是")
    private Integer isSystem;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "修改时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "修改人ID")
    private Long updaterId;


    @PrePersist
    private void onCreate() {

        if (this.id == null) {
            this.id = IdWorker.nextId();
        }

        if (this.status == null) {
            this.status = 1; // 默认启用
        }

        if (this.isSystem == null) {
            this.isSystem = 0; // 默认非系统组
        }

        if (this.seq == null) {
            this.seq = 0; // 默认排序号
        }

        LocalDateTime now = LocalDateTime.now();

        if (this.createTime == null) {
            this.createTime = now;
        }

        if (this.updateTime == null) {
            this.updateTime = now;
        }

    }

    @PreUpdate
    private void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }
} 