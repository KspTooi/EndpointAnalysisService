package com.ksptool.bio.biz.outmodelorigin.model;

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
@Table(name = "gen_out_model_origin")
public class OutModelOriginPo {

    @Column(name = "id", comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "output_schema_id", nullable = false, comment = "输出方案ID")
    private Long outputSchemaId;

    @Column(name = "name", nullable = false, length = 255, comment = "原始字段名")
    private String name;

    @Column(name = "kind", nullable = false, length = 255, comment = "原始数据类型")
    private String kind;

    @Column(name = "length", length = 255, comment = "原始长度")
    private String length;

    @Column(name = "require", nullable = false, columnDefinition = "TINYINT", comment = "原始必填 0:否 1:是")
    private Integer require;

    @Column(name = "remark", length = 255, comment = "原始备注")
    private String remark;

    @Column(name = "seq", nullable = false, comment = "原始排序")
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
