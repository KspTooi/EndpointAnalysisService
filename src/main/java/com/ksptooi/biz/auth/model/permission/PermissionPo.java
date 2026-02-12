package com.ksptooi.biz.auth.model.permission;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 权限实体类
 * 用于管理系统中的权限节点信息
 * 采用平级设计，权限标识使用 : 分隔表示层级
 * 例如：system:user:view, system:user:edit
 */
@Entity
@Table(name = "core_permission", comment = "权限表")
@Getter
@Setter
public class PermissionPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "权限ID")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 100, comment = "权限标识，如：system:user:view")
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 50, comment = "权限名称，如：查看用户")
    private String name;

    @Column(name = "description", length = 200, comment = "权限描述")
    private String description;

    @Column(name = "sort_order", nullable = false, comment = "排序号")
    private Integer sortOrder;

    @Column(name = "is_system", nullable = false, comment = "是否系统权限 0:非系统权限 1:系统权限")
    private Integer isSystem = 0;

    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "修改时间")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        if (sortOrder == null) {
            sortOrder = 0;
        }
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }

    /**
     * 判断两个权限是否相等 只要id相等就认为相等
     *
     * @param obj 要比较的权限
     * @return 是否相等
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PermissionPo other = (PermissionPo) obj;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
} 