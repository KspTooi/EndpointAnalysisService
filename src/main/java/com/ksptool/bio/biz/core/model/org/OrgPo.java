package com.ksptool.bio.biz.core.model.org;


import com.ksptool.bio.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import static com.ksptool.bio.biz.auth.service.SessionService.session;

@Getter
@Setter
@Entity
@Table(name = "core_org")
@SQLRestriction(value = "delete_time IS NULL")
@SQLDelete(sql = "UPDATE core_org SET delete_time = NOW() WHERE id = ?")
public class OrgPo {

    @Column(name = "id", comment = "主键id")
    @Id
    private Long id;

    @Column(name = "root_id", nullable = false, comment = "一级组织ID")
    private Long rootId;

    @Column(name = "parent_id", comment = "上级组织ID NULL顶级组织")
    private Long parentId;

    @Column(name = "org_path_ids", length = 1024, comment = "从顶级组织到当前组织的ID列表 以,分割")
    private String orgPathIds;

    @Column(name = "kind", nullable = false, columnDefinition = "tinyint", comment = "0:部门 1:企业")
    private Integer kind;

    @Column(name = "name", nullable = false, length = 128, comment = "组织机构名称")
    private String name;

    @Column(name = "principal_id", comment = "主管ID")
    private Long principalId;

    @Column(name = "principal_name", length = 32, comment = "主管名称")
    private String principalName;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人id")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "更新人id")
    private Long updaterId;

    @Column(name = "delete_time", comment = "删除时间 NULL未删除")
    private LocalDateTime deleteTime;

    @PrePersist
    private void onCreate() throws AuthException {

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
            this.creatorId = session().getUserId();
        }

        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }
    }

    @PreUpdate
    private void onUpdate() throws AuthException {

        this.updateTime = LocalDateTime.now();

        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }
    }
}
