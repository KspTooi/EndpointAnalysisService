package com.ksptooi.biz.document.model.epstdword;

import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import static com.ksptool.bio.biz.auth.service.SessionService.session;

/**
 * 标准词管理
 *
 * @author: generator
 */
@Getter
@Setter
@Entity
@Table(name = "ep_std_word", comment = "标准词管理", indexes = {
        @Index(name = "uk_source_name_delete", unique = true, columnList = "source_name,delete_time")
})
@SQLDelete(sql = "UPDATE ep_std_word SET delete_time = NOW() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
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

    @Column(name = "source_name_py_idx", length = 320, comment = "简称 拼音首字母索引")
    private String sourceNamePyIdx;

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
    private void onUpdate() throws AuthException {
        this.updateTime = LocalDateTime.now();
        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }
    }
}
