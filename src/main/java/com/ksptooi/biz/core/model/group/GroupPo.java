package com.ksptooi.biz.core.model.group;

import com.ksptooi.biz.core.model.permission.PermissionPo;
import com.ksptooi.biz.core.model.user.UserPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户组实体类
 * 用于管理系统中的用户组信息，对用户进行分组并分配权限
 */

@Entity
@Table(name = "core_group")
@Getter
@Setter
@Comment("用户组")
public class GroupPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("组ID")
    private Long id;

    @Column(name = "code", nullable = false, unique = true, length = 50)
    @Comment("组标识，如：admin、developer等")
    private String code;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @Comment("组名称，如：管理员组、开发者组等")
    private String name;

    @Column(name = "description", length = 200)
    @Comment("组描述")
    private String description;

    @Column(name = "is_system", nullable = false)
    @Comment("是否系统内置组（内置组不可删除）")
    private Boolean isSystem;

    @Column(name = "status", nullable = false)
    @Comment("组状态：0-禁用，1-启用")
    private Integer status;

    @Column(name = "sort_order", nullable = false)
    @Comment("排序号")
    private Integer sortOrder;

    @Column(name = "create_user_id")
    @Comment("创建人ID")
    private Long createUserId;

    @Column(name = "update_user_id")
    @Comment("修改人ID")
    private Long updateUserId;

    @Column(name = "create_time", nullable = false, updatable = false)
    @Comment("创建时间")
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("修改时间")
    private Date updateTime;

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
        createTime = new Date();
        updateTime = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = new Date();
    }
} 