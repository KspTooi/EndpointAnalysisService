package com.ksptool.bio.biz.zremovedauthgroup.model;

import java.time.LocalDateTime;
import com.ksptool.bio.biz.auth.service.AuthService;
import com.ksptool.bio.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "zremoved_auth_group")
public class ZremovedAuthGroupPo {

    @Column(name = "id", comment = "组ID")
    @Id
    private Long id;

    @Column(name = "code", comment = "组标识，如：admin、developer等")
    private String code;

    @Column(name = "name", comment = "组名称，如：管理员组、开发者组等")
    private String name;

    @Column(name = "remark", nullable = true, comment = "组描述")
    private String remark;

    @Column(name = "status", comment = "组状态:0:禁用，1:启用")
    private Integer status;

    @Column(name = "seq", comment = "排序号")
    private Integer seq;

    @Column(name = "is_system", comment = "系统内置组 0:否 1:是")
    private Integer isSystem;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", comment = "修改时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", comment = "修改人ID")
    private Long updaterId;


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
    }

    @PreUpdate
    private void onUpdate() {
        
        this.updateTime = LocalDateTime.now();
        
        if (this.updaterId == null) {
            this.updaterId = AuthService.getCurrentUserId();
        }
    }
}
