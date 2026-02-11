package com.ksptooi.biz.core.model.exceltemplate;


import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import static com.ksptooi.biz.auth.service.SessionService.session;

/**
 * ${tableComment}
 *
 * @author: generator
 * @date: ${.now?string("yyyy年MM月dd日 HH:mm")}
 */
@Getter
@Setter
@Entity
@Table(name = "core_excel_template", indexes = {
        @Index(name = "uk_code_delete_time", columnList = "code,delete_time", unique = true)
})
@SQLDelete(sql = "UPDATE core_excel_template SET delete_time = now() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class ExcelTemplatePo {

    @Id
    @Column(name = "id", nullable = false, comment = "模板ID")
    private Long id;

    @Column(name = "attach_id", nullable = false, comment = "模板附件ID")
    private Long attachId;

    @Column(name = "name", nullable = false, length = 32, comment = "模板名称")
    private String name;

    @Column(name = "code", nullable = false, length = 32, comment = "模板标识 唯一")
    private String code;

    @Column(name = "remark", columnDefinition = "text", comment = "模板备注")
    private String remark;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint", comment = "状态 0:启用 1:禁用")
    private Integer status;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "更新人ID")
    private Long updaterId;

    @Column(name = "delete_time", comment = "删除时间 NULL未删除")
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
            this.creatorId = session().getUserId();
        }
        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
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
