package com.ksptool.bio.biz.drive.model;

import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.core.model.attach.AttachPo;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;

import static com.ksptool.bio.biz.auth.service.SessionService.session;


@Getter
@Setter
@Entity
@Table(name = "drive_entry")
@SQLDelete(sql = "UPDATE drive_entry SET delete_time = now() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class EntryPo {

    @Column(name = "id", comment = "条目ID")
    @Id
    private Long id;

    @Column(name = "company_id", nullable = false, comment = "团队ID")
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), comment = "父级ID 为NULL顶级")
    private EntryPo parent;

    @Column(name = "name", length = 128, nullable = false, comment = "条目名称")
    private String name;

    @Column(name = "kind", comment = "条目类型 0:文件 1:文件夹")
    private Integer kind;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attach_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), comment = "文件附件ID 文件夹为NULL")
    private AttachPo attach;

    @Column(name = "attach_size", comment = "文件附件大小 文件夹为0")
    private Long attachSize;

    @Column(name = "attach_suffix", length = 128, comment = "文件附件类型 文件夹为NULL")
    private String attachSuffix;

    @Column(name = "attach_status", columnDefinition = "tinyint", comment = "文件附件状态 0:预检文件 1:区块不完整 2:校验中 3:有效")
    private Integer attachStatus;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "delete_time", comment = "删除时间 为NULL未删除")
    private LocalDateTime deleteTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人")
    private Long creatorId;

    @Column(name = "updater_id", nullable = false, comment = "更新人")
    private Long updaterId;

    //子级条目
    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntryPo> children;

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
