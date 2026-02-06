package com.ksptooi.biz.core.model.noticetemplate;

import com.ksptooi.biz.core.service.SessionService;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "core_notice_template")
public class NoticeTemplatePo {

    @Column(name = "id", nullable = false, comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "name", length = 32, nullable = false, comment = "模板名称")
    private String name;

    @Column(name = "code", length = 32, nullable = false, comment = "模板唯一编码 (业务调用用)")
    private String code;

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = false, comment = "模板内容 (含占位符)")
    private String content;

    @Column(name = "status", columnDefinition = "TINYINT", nullable = false, comment = "状态: 0启用, 1禁用")
    private Integer status;

    @Column(name = "remark", length = 1000, nullable = true, comment = "备注")
    private String remark;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "更新人ID")
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
