package com.ksptool.bio.biz.core.model.company;

import com.ksptool.bio.biz.core.model.companymember.CompanyMemberPo;
import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import com.ksptool.assembly.entity.exception.BizException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.LastModifiedBy;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "core_company")
@SQLDelete(sql = "UPDATE core_company SET deleted_time = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_time IS NULL")
public class CompanyPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", comment = "公司ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "founder_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "创始人")
    private UserPo founder;

    @Column(name = "name", nullable = false, length = 32, comment = "公司名")
    private String name;

    @Column(name = "description", length = 5000, columnDefinition = "longtext", comment = "公司描述")
    private String description;

    @Column(name = "deleted_time", comment = "移除时间 为null代表未删除")
    private LocalDateTime deletedTime;

    @CreatedDate
    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @CreatedBy
    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompanyMemberPo> members = new HashSet<>();


    /**
     * 添加公司成员
     *
     * @param user 用户
     * @param role 角色 0:CEO 1:成员
     */
    public void addMember(UserPo user, Integer role) throws BizException {

        if (members == null) {
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

    }

    @PreUpdate
    private void onUpdate() {

    }
}
