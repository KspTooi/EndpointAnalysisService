package com.ksptool.bio.biz.qfmodelgroup.model;

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
@Table(name = "qf_model_group")
@SQLRestriction("delete_time IS NULL")
@SQLDelete(sql = "UPDATE qf_model_group SET delete_time = NOW() WHERE id = ?")
public class QfModelGroupPo {

    @Id
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "root_id", nullable = false, comment = "所属企业/租户ID")
    private Long rootId;

    @Column(name = "dept_id", nullable = false, comment = "所属部门ID")
    private Long deptId;

    @Column(name = "name", nullable = false, length = 80, comment = "组名称")
    private String name;

    @Column(name = "code", nullable = false, length = 32, comment = "组编码")
    private String code;

    @Column(name = "remark", columnDefinition = "TEXT", comment = "备注")
    private String remark;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

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

        var now = LocalDateTime.now();
        var session = SessionService.session();

        if (this.createTime == null) {
            this.createTime = now;
        }

        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }

        if (this.creatorId == null) {
            this.creatorId = session.getUserId();
        }

        if (this.updaterId == null) {
            this.updaterId = session.getUserId();
        }

        if(this.rootId == null){
            this.rootId = session.getRootId();
        }
        if(this.deptId == null){
            this.deptId = session.getDeptId();
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
