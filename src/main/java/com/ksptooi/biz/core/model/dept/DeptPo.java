package com.ksptooi.biz.core.model.dept;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import java.time.LocalDateTime;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

/**
 * ${tableComment}
 *
 * @author: generator
 * @date: ${.now?string("yyyy年MM月dd日 HH:mm")}
 */
@Getter
@Setter
@Entity
@Table(name = "core_dept")
@SQLDelete(sql = "UPDATE core_dept SET delete_time = NOW() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class DeptPo {

    @Column(name = "id")
    @Id
    @Comment("部门ID")
    private Long id;

    @Column(name = "parent_id")
    @Comment("父级部门 NULL为顶级")
    private Long parentId;

    @Column(name = "name", length = 32, nullable = false)
    @Comment("部门名")
    private String name;

    @Column(name = "principal_id")
    @Comment("负责人ID")
    private String principalId;

    @Column(name = "principal_name")
    @Comment("负责人名称")
    private String principalName;

    @Column(name = "status", columnDefinition = "tinyint")
    @Comment("部门状态 0:正常 1:禁用")
    private Integer status;

    @Column(name = "seq")
    @Comment("排序")
    private Integer seq;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false)
    @Comment("创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false)
    @Comment("更新人ID")
    private Long updaterId;

    @Column(name = "delete_time")
    @Comment("删除时间 为NULL未删除")
    private LocalDateTime deleteTime;

    @PrePersist
    private void onCreate() {

        if (this.id == null) {
            this.id = IdWorker.nextId();
        }

        LocalDateTime now = LocalDateTime.now();
        if (this.createTime == null) {
            this.createTime = now;
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
        if (this.deleteTime == null) {
            this.deleteTime = null;
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
