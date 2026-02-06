package com.ksptooi.biz.core.model.user;

import com.ksptooi.biz.core.model.attach.AttachPo;
import com.ksptooi.biz.core.model.company.CompanyPo;
import com.ksptooi.biz.core.model.companymember.CompanyMemberPo;
import com.ksptooi.biz.core.model.group.GroupPo;
import com.ksptooi.biz.rdbg.model.userrequestenv.UserRequestEnvPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "core_user", comment = "用户表", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_username", columnNames = {"username"})
})
@Getter
@Setter
@SQLDelete(sql = "UPDATE core_user SET delete_time = NOW() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class UserPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "用户ID")
    private Long id;

    @Column(name = "username", nullable = false, length = 50, comment = "用户名")
    private String username;

    @Column(name = "password", nullable = false, length = 1280, comment = "密码")
    private String password;

    @Column(name = "nickname", length = 50, comment = "昵称")
    private String nickname;

    @Column(name = "gender", columnDefinition = "tinyint", comment = "性别 0:男 1:女 2:不愿透露")
    private Integer gender;

    @Column(name = "phone", length = 64, comment = "手机号")
    private String phone;

    @Column(name = "email", length = 64, comment = "邮箱")
    private String email;

    @Column(name = "login_count", nullable = false, comment = "登录次数")
    private Integer loginCount;

    @Column(name = "status", nullable = false, comment = "用户状态 0:正常 1:封禁")
    private Integer status;

    @Column(name = "last_login_time", comment = "最后登录时间")
    private LocalDateTime lastLoginTime;

    @Column(name = "root_id", comment = "所属企业ID")
    private Long rootId;

    @Column(name = "dept_id", comment = "所属部门ID")
    private Long deptId;

    @Column(name = "root_name", length = 32, comment = "所属企业名")
    private String rootName;

    @Column(name = "dept_name", length = 255, comment = "所属部门名")
    private String deptName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_company_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "已激活的公司 为null时表示未激活任何公司")
    private CompanyPo activeCompany;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_env_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "已激活的环境 为null时表示未激活任何环境")
    private UserRequestEnvPo activeEnv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_attach_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "用户头像附件")
    private AttachPo avatarAttach;

    @Column(name = "is_system", columnDefinition = "tinyint", nullable = false, comment = "是否为系统用户 0:否 1:是")
    private Integer isSystem;

    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "修改时间")
    private LocalDateTime updateTime;

    @Column(name = "delete_time", comment = "删除时间 为null代表未删除")
    private LocalDateTime deleteTime;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "core_user_group",
            joinColumns = @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)),
            inverseJoinColumns = @JoinColumn(name = "group_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    )
    private Set<GroupPo> groups = new HashSet<>();
    
    @OneToMany(mappedBy = "founder", fetch = FetchType.LAZY)
    private Set<CompanyPo> createdCompanies = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private Set<CompanyMemberPo> companyMemberships = new HashSet<>();


    @PrePersist
    public void prePersist() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
        if (loginCount == null) {
            loginCount = 0;
        }
        if (status == null) {
            status = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }


    /**
     * 获取当前用户激活的公司ID
     *
     * @return 当前用户激活的公司ID，如果未激活任何公司则返回null
     */
    public Long getActiveCompanyId() {
        if (activeCompany == null) {
            return null;
        }
        return activeCompany.getId();
    }

}