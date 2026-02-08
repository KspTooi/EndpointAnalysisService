package com.ksptooi.biz.core.model.registrynode;

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
@Table(name = "core_registry_node")
@SQLDelete(sql = "UPDATE core_registry_node SET delete_time = NOW() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class RegistryNodePo {

    @Column(name = "id", nullable = false, comment = "ID")
    @Id
    private Long id;

    @Column(name = "parent_id", comment = "父级项ID NULL顶级")
    private Long parentId;

    @Column(name = "key_path", length = 1024, nullable = false, comment = "节点Key的全路径")
    private String keyPath;

    @Column(name = "nkey", length = 128, nullable = false, comment = "节点Key")
    private String nkey;

    @Column(name = "label", length = 32, comment = "节点标签")
    private String label;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "remark", columnDefinition = "TEXT", comment = "说明")
    private String remark;

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
