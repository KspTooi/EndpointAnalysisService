package com.ksptooi.biz.core.model.org;


import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
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
@Table(name = "core_org")
@SQLRestriction(value = "delete_time IS NULL")
@SQLDelete(sql = "UPDATE core_org SET delete_time = NOW() WHERE id = ?")
public class OrgPo {

    @Column(name = "id")
    @Id
    @Comment("主键id")
    private Long id;

    @Column(name = "root_id", nullable = false)
    @Comment("一级组织ID")
    private Long rootId;

    @Column(name = "parent_id")
    @Comment("上级组织ID NULL顶级组织")
    private Long parentId;

    @Column(name = "kind", nullable = false, columnDefinition = "tinyint")
    @Comment("0:部门 1:企业")
    private Integer kind;

    @Column(name = "name", nullable = false, length = 128)
    @Comment("组织机构名称")
    private String name;

    @Column(name = "principal_id")
    @Comment("主管ID")
    private Long principalId;

    @Column(name = "principal_name", length = 32)
    @Comment("主管名称")
    private String principalName;

    @Column(name = "seq", nullable = false)
    @Comment("排序")
    private Integer seq;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false)
    @Comment("创建人id")
    private Long creatorId;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false)
    @Comment("更新人id")
    private Long updaterId;

    @Column(name = "delete_time")
    @Comment("删除时间 NULL未删除")
    private LocalDateTime deleteTime;

    @PrePersist
    private void onCreate() {

        if (this.id == null) {
            this.id = IdWorker.nextId();
        }


        if (this.createTime == null) {
            this.createTime = LocalDateTime.now();
        }

        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }

        if (this.creatorId == null) {
            this.creatorId = AuthService.getCurrentUserId();
        }

        if (this.updaterId == null) {
            this.updaterId = AuthService.getCurrentUserId();
        }
    }

    @PreUpdate
    private void onUpdate() {

        this.updateTime = LocalDateTime.now();

        if (this.updaterId == null) {
            this.updaterId = AuthService.getCurrentUserId();
        }
    }
}
