package com.ksptooi.biz.core.model.registryentry;

import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.ksptooi.biz.core.service.SessionService.session;

@Getter
@Setter
@Entity
@Table(name = "registry_entry")
public class RegistryEntryPo {

    @Column(name = "id", comment = "值ID")
    @Id
    private Long id;

    @Column(name = "node_id", comment = "节点ID")
    private Long nodeId;

    @Column(name = "node_key_path", comment = "项全键")
    private String nodeKeyPath;

    @Column(name = "ekey", comment = "条目Key")
    private String ekey;

    @Column(name = "value_key_path", comment = "节点Key的全路径")
    private String valueKeyPath;

    @Column(name = "value_kind", comment = "数据类型 0:字串 1:整数 2:浮点 3:日期(LDT)")
    private Integer valueKind;

    @Column(name = "value", nullable = true, comment = "条目值")
    private String value;

    @Column(name = "label", nullable = true, comment = "条目标签")
    private String label;

    @Column(name = "metadata", comment = "元数据JSON")
    private String metadata;

    @Column(name = "seq", comment = "排序")
    private Integer seq;

    @Column(name = "status", comment = "状态 0:正常 1:停用")
    private Integer status;

    @Column(name = "is_system", comment = "内置值 0:否 1:是")
    private Integer isSystem;

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
    private void onCreate() throws Exception {

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
