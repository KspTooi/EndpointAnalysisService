package com.ksptooi.biz.core.model.post;

import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

import static com.ksptooi.biz.auth.service.SessionService.session;

@Getter
@Setter
@Entity
@Table(name = "core_post")
@SQLRestriction("delete_time IS NULL")
@SQLDelete(sql = "UPDATE core_post SET delete_time = NOW() WHERE id = ?")
public class PostPo {

    @Column(name = "id", nullable = false, comment = "岗位ID")
    @Id
    private Long id;

    @Column(name = "name", length = 32, nullable = false, comment = "岗位名称")
    private String name;

    @Column(name = "code", length = 32, nullable = false, comment = "岗位编码")
    private String code;

    @Column(name = "seq", nullable = false, comment = "岗位排序")
    private Integer seq;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT", comment = "0:启用 1:停用")
    private Integer status;

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
