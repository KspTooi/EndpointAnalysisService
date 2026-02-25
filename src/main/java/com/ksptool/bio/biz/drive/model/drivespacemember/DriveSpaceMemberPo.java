package com.ksptool.bio.biz.drive.model.drivespacemember;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.auth.service.SessionService;
import com.ksptool.bio.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "drive_space_member")
public class DriveSpaceMemberPo {

    @Column(name = "id", comment = "空间ID")
    @Id
    private Long id;

    @Column(name = "member_kind", comment = "成员类型 0:用户 1:部门")
    private Integer memberKind;

    @Column(name = "member_id", comment = "成员ID")
    private Long memberId;

    @Column(name = "role", comment = "成员角色 0:主管理员 1:行政管理员 2:编辑者 3:查看者")
    private Integer role;

    @Column(name = "create_time", comment = "加入时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", comment = "邀请人/操作人ID")
    private Long creatorId;

    @Column(name = "update_time", comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", comment = "更新人")
    private Long updaterId;


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
