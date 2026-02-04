package com.ksptooi.biz.document.model.epstdword;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 标准词管理
 *
 * @author: generator
 */
@Getter
@Setter
@Entity
@Table(name = "ep_std_word", comment = "标准词管理")
public class EpStdWordPo {

    @Id
    @Column(name = "id", nullable = false, comment = "标准词ID")
    private Long id;

    @Column(name = "source_name", nullable = false, length = 128, comment = "简称")
    private String sourceName;

    @Column(name = "source_name_full", length = 255, comment = "全称")
    private String sourceNameFull;

    @Column(name = "target_name", nullable = false, length = 128, comment = "英文简称")
    private String targetName;

    @Column(name = "target_name_full", length = 128, comment = "英文全称")
    private String targetNameFull;

    @Column(name = "remark", columnDefinition = "text", comment = "备注")
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
