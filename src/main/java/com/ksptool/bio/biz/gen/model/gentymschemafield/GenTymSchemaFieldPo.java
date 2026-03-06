package com.ksptool.bio.biz.gen.model.gentymschemafield;

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
@Table(name = "gen_tym_schema_field")
public class GenTymSchemaFieldPo {

    @Id
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "type_schema_id", nullable = false, comment = "类型映射方案ID")
    private Long typeSchemaId;

    @Column(name = "source", nullable = false, length = 80, comment = "匹配源类型")
    private String source;

    @Column(name = "target", nullable = false, length = 80, comment = "匹配目标类型")
    private String target;

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
