package com.ksptool.bio.biz.core.model.companymember;

import com.ksptool.bio.biz.core.model.company.CompanyPo;
import com.ksptool.bio.biz.core.model.user.UserPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "core_company_member", uniqueConstraints = {
        @UniqueConstraint(name = "uk_company_user", columnNames = {"company_id", "user_id", "deleted_time"})
})
@SQLDelete(sql = "UPDATE core_company_member SET deleted_time = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_time IS NULL")
public class CompanyMemberPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", comment = "记录ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "公司")
    private CompanyPo company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "用户")
    private UserPo user;

    @Column(name = "role", columnDefinition = "tinyint", comment = "职务 0:CEO 1:成员")
    private Integer role;

    @Column(name = "joined_time", nullable = false, comment = "加入时间")
    private LocalDateTime joinedTime;

    @Column(name = "deleted_time", nullable = true, comment = "删除时间 为NULL时代表未删除")
    private LocalDateTime deletedTime;

    @PrePersist
    private void onCreate() {
        if (this.joinedTime == null) {
            this.joinedTime = LocalDateTime.now();
        }
    }

}
