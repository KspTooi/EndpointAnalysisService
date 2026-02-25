package com.ksptool.bio.biz.drivespace.model;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.auth.service.SessionService;
import com.ksptool.bio.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "drive_space")
@SQLRestriction("delete_time IS NULL")
@SQLDelete(sql = "UPDATE drive_space SET delete_time = NOW() WHERE id = ?")
public class DriveSpacePo {

    @Id
    @Column(name = "id", comment = "空间ID", nullable = false)
    private Long id;

    @Column(name = "root_id", comment = "租户ID", nullable = false)
    private Long rootId;

    @Column(name = "dept_id", comment = "部门ID", nullable = false)
    private Long deptId;

    @Column(name = "name", comment = "空间名称", nullable = false, length = 80)
    private String name;

    @Column(name = "remark", comment = "空间描述", columnDefinition = "TEXT")
    private String remark;

    @Column(name = "quota_limit", comment = "配额限制(bytes)", nullable = false)
    private Long quotaLimit;

    @Column(name = "quota_used", comment = "已用配额(bytes)", nullable = false)
    private Long quotaUsed;

    @Column(name = "status", comment = "状态 0:正常 1:归档", nullable = false, columnDefinition = "TINYINT")
    private Integer status;

    @Column(name = "create_time", comment = "创建时间", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "creator_id", comment = "创建人", nullable = false)
    private Long creatorId;

    @Column(name = "update_time", comment = "更新时间", nullable = false)
    private LocalDateTime updateTime;

    @Column(name = "updater_id", comment = "更新人", nullable = false)
    private Long updaterId;

    @Column(name = "delete_time", comment = "删除时间 为NULL未删除")
    private LocalDateTime deleteTime;


    @PrePersist
    private void onCreate() throws AuthException {

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
            this.creatorId = SessionService.session().getUserId();
        }

        if (this.updaterId == null) {
            this.updaterId = SessionService.session().getUserId();
        }
    }

    @PreUpdate
    private void onUpdate() throws AuthException {

        this.updateTime = LocalDateTime.now();

        if (this.updaterId == null) {
            this.updaterId = SessionService.session().getUserId();
        }
    }
}
