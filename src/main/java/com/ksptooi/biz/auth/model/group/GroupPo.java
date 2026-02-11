package com.ksptooi.biz.auth.model.group;

import com.ksptooi.biz.auth.model.permission.PermissionPo;
import com.ksptooi.biz.core.model.user.UserPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户组实体类
 * 用于管理系统中的用户组信息，对用户进行分组并分配权限
 */

@Entity
@Table(name = "core_group", comment = "用户组")
@Getter
@Setter
public class GroupPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "组ID")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50, comment = "组标识，如：admin、developer等")
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 50, comment = "组名称，如：管理员组、开发者组等")
    private String name;

    @Column(name = "description", length = 200, comment = "组描述")
    private String description;

    @Column(name = "is_system", nullable = false, comment = "是否系统内置组（内置组不可删除）")
    private Boolean isSystem;

    @Column(name = "status", nullable = false, comment = "组状态：0-禁用，1-启用")
    private Integer status;

    @Column(name = "sort_order", nullable = false, comment = "排序号")
    private Integer sortOrder;

    @Column(name = "create_user_id", comment = "创建人ID")
    private Long createUserId;

    @Column(name = "update_user_id", comment = "修改人ID")
    private Long updateUserId;

    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "修改时间")
    private LocalDateTime updateTime;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "core_group_permission",
            joinColumns = @JoinColumn(name = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "permission_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    )
    //用户组拥有的权限
    private Set<PermissionPo> permissions = new HashSet<>();

    @ManyToMany(mappedBy = "groups", fetch = FetchType.LAZY)
    //用户组中的用户
    private Set<UserPo> users = new HashSet<>();

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = 1; // 默认启用
        }
        if (isSystem == null) {
            isSystem = false; // 默认非系统组
        }
        if (sortOrder == null) {
            sortOrder = 0; // 默认排序号
        }
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }
} 