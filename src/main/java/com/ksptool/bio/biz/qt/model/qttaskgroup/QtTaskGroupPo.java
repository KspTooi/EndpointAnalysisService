package com.ksptool.bio.biz.qt.model.qttaskgroup;

import com.ksptool.bio.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.ksptool.bio.biz.auth.service.SessionService.session;

@Getter
@Setter
@Entity
@Table(name = "qt_task_group")
public class QtTaskGroupPo {

    @Column(name = "id")
    @Id
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 80, comment = "分组名")
    private String name;

    @Column(name = "remark", length = 1000, comment = "分组备注")
    private String remark;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "更新人ID")
    private Long updaterId;

    @Column(name = "delete_time", comment = "删除时间 NULL未删")
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

        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }

        if (this.creatorId == null) {
            this.creatorId = session().getUserId();
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
