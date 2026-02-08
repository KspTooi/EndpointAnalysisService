package com.ksptooi.biz.core.model.registry;

import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import static com.ksptooi.biz.core.service.SessionService.session;

@Getter
@Setter
@Entity
@Table(name = "core_registry")
@SQLDelete(sql = "UPDATE core_registry SET delete_time = NOW() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class RegistryPo {

    @Column(name = "id", nullable = false, comment = "ID")
    @Id
    private Long id;

    @Column(name = "parent_id", comment = "父级ID NULL顶级")
    private Long parentId;

    @Column(name = "key_path", length = 1024, nullable = false, comment = "KEY的全路径")
    private String keyPath;

    @Column(name = "nkey", length = 128, nullable = false, comment = "键")
    private String nkey;

    @Column(name = "nvalue_kind", columnDefinition = "TINYINT", nullable = false, comment = "数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    private Integer nvalueKind;

    @Column(name = "nvalue", columnDefinition = "LONGTEXT", comment = "值")
    private String nvalue;

    @Column(name = "label", length = 512, comment = "标签")
    private String label;

    @Column(name = "status", columnDefinition = "TINYINT", nullable = false, comment = "状态 0:正常 1:停用")
    private Integer status;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "remark", columnDefinition = "TEXT", comment = "说明")
    private String remark;

    @Column(name = "metadata", columnDefinition = "JSON", nullable = false, comment = "元数据JSON")
    private String metadata;

    @Column(name = "is_system", columnDefinition = "TINYINT", nullable = false, comment = "内置项 0:否 1:是")
    private Integer isSystem;

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
    private void onUpdate() throws Exception {

        this.updateTime = LocalDateTime.now();

        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }
    }
}
