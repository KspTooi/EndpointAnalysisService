package com.ksptooi.biz.core.model.companymember;

import com.ksptooi.biz.core.model.company.CompanyPo;
import com.ksptooi.biz.core.model.user.UserPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "core_company_member")
@SQLDelete(sql = "UPDATE core_company_member SET deleted_time = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted_time IS NULL")
public class CompanyMemberPo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @Comment("记录ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("公司")
    private CompanyPo company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("用户")
    private UserPo user;

    @Column(name = "role", columnDefinition = "tinyint")
    @Comment("职务 0:CEO 1:成员")
    private Integer role;

    @Column(name = "joined_time", nullable = false)
    @Comment("加入时间")
    private LocalDateTime joinedTime;

    @Column(name = "deleted_time", nullable = true)
    @Comment("删除时间 为NULL时代表未删除")
    private LocalDateTime deletedTime;

    @PrePersist
    private void onCreate() {
        if (this.joinedTime == null) {
            this.joinedTime = LocalDateTime.now();
        }
    }

}
