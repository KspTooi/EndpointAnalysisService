package com.ksptool.bio.biz.gen.model.gentymschema;

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
@Table(name = "gen_tym_schema")
public class GenTymSchemaPo {

    @Id
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 32, comment = "方案名称")
    private String name;

    @Column(name = "code", nullable = false, length = 32, comment = "方案编码")
    private String code;

    @Column(name = "map_source", nullable = false, length = 32, comment = "映射源")
    private String mapSource;

    @Column(name = "map_target", nullable = false, length = 32, comment = "映射目标")
    private String mapTarget;

    @Column(name = "type_count", nullable = false, comment = "类型数量")
    private Integer typeCount;

    @Column(name = "default_type", nullable = false, length = 80, comment = "默认类型")
    private String defaultType;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "remark", columnDefinition = "TEXT", comment = "备注")
    private String remark;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "更新人ID")
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
