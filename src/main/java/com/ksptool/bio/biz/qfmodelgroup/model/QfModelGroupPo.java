package com.ksptool.bio.biz.qfmodelgroup.model;

import com.ksptool.assembly.entity.exception.AuthException;
import java.time.LocalDateTime;
import com.ksptool.bio.biz.auth.service.SessionService;
import com.ksptool.bio.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "qf_model_group")
public class QfModelGroupPo {

    @Column(name = "id", comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "root_id", comment = "所属企业/租户ID")
    private Long rootId;

    @Column(name = "dept_id", comment = "所属部门ID")
    private Long deptId;

    @Column(name = "name", comment = "组名称")
    private String name;

    @Column(name = "code", comment = "组编码")
    private String code;

    @Column(name = "remark", nullable = true, comment = "备注")
    private String remark;

    @Column(name = "seq", comment = "排序")
    private Integer seq;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", comment = "更新人ID")
    private Long updaterId;

    @Column(name = "delete_time", nullable = true, comment = "删除时间 NULL未删")
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
