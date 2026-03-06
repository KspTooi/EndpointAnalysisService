package com.ksptool.bio.biz.gentymschema.model;

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
@Table(name = "gen_tym_schema")
public class GenTymSchemaPo {

    @Column(name = "id", comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "name", comment = "方案名称")
    private String name;

    @Column(name = "code", comment = "方案编码")
    private String code;

    @Column(name = "map_source", comment = "映射源")
    private String mapSource;

    @Column(name = "map_target", comment = "映射目标")
    private String mapTarget;

    @Column(name = "type_count", comment = "类型数量")
    private Integer typeCount;

    @Column(name = "default", comment = "默认类型")
    private String default;

    @Column(name = "seq", comment = "排序")
    private Integer seq;

    @Column(name = "remark", nullable = true, comment = "备注")
    private String remark;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", comment = "更新人ID")
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
