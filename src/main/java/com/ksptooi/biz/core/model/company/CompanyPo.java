package com.ksptooi.biz.core.model.company;

import com.ksptooi.biz.core.model.companymember.CompanyMemberPo;
import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.commons.exception.BizException;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "core_company")
@SQLDelete(sql = "UPDATE core_company SET deleted_time = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_time IS NULL")
public class CompanyPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("公司ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "founder_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("创始人")
    private UserPo founder;

    @Column(name = "name", nullable = false, length = 32)
    @Comment("公司名")
    private String name;

    @Column(name = "description", length = 5000, columnDefinition = "longtext")
    @Comment("公司描述")
    private String description;

    @Column(name = "deleted_time")
    @Comment("移除时间 为null代表未删除")
    private LocalDateTime deletedTime;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("公司成员")
    private Set<CompanyMemberPo> members = new HashSet<>();


    /**
     * 添加公司成员
     * @param user 用户
     * @param role 角色 0:CEO 1:成员
     */
    public void addMember(UserPo user, Integer role) throws BizException {

        if(members == null){
            members = new HashSet<>();
        }

        if (members.stream().anyMatch(m -> m.getUser().getId().equals(user.getId()))) {
            throw new BizException("用户已加入公司");
        }
        CompanyMemberPo member = new CompanyMemberPo();
        member.setCompany(this);
        member.setUser(user);
        member.setRole(role);
        members.add(member);
    }


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
