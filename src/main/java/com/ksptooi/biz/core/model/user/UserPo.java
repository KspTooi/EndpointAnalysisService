package com.ksptooi.biz.core.model.user;

import com.ksptooi.biz.core.model.group.GroupPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@Comment("用户表")
public class UserPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("用户ID")
    private Long id;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    @Comment("用户名")
    private String username;

    @Column(name = "password", nullable = false, length = 1280)
    @Comment("密码")
    private String password;

    @Column(name = "email", length = 64)
    @Comment("邮箱")
    private String email;

    @Column(name = "nickname", length = 50)
    @Comment("昵称")
    private String nickname;

    @Column(name = "login_count", nullable = false)
    @Comment("登录次数")
    private Integer loginCount;

    @Column(name = "status", nullable = false)
    @Comment("用户状态 0:正常 1:封禁")
    private Integer status;

    @Column(name = "last_login_time")
    @Comment("最后登录时间")
    private Date lastLoginTime;

    @Column(name = "create_time", nullable = false, updatable = false)
    @Comment("创建时间")
    private Date createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("修改时间")
    private Date updateTime;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_group",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    )
    @Comment("用户所属的用户组")
    private Set<GroupPo> groups = new HashSet<>();


    @PrePersist
    public void prePersist() {
        createTime = new Date();
        updateTime = new Date();
        if (loginCount == null) {
            loginCount = 0;
        }
        if (status == null) {
            status = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = new Date();
    }


}