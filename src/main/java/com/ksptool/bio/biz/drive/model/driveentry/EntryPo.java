package com.ksptool.bio.biz.drive.model.driveentry;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.auth.common.aop.RowScopePo;
import com.ksptool.bio.biz.core.model.attach.AttachPo;
import com.ksptool.bio.commons.utils.IdWorker;
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
@SQLDelete(sql = "UPDATE drive_entry SET delete_time = NOW() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class EntryPo extends RowScopePo {

    @Column(name = "id", comment = "条目ID")
    @Id
    private Long id;

    @Column(name = "root_id", nullable = false, comment = "公司/租户ID")
    private Long rootId;

    @Column(name = "dept_id", nullable = false, comment = "部门ID")
    private Long deptId;

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

    @Column(name = "creator_id", nullable = false, comment = "创建人")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "更新人")
    private Long updaterId;

    @Column(name = "delete_time", comment = "删除时间 为NULL未删除")
    private LocalDateTime deleteTime;


    //子级条目
    @BatchSize(size = 100)
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EntryPo> children;

    @PrePersist
    private void onCreate() throws AuthException {

        if (this.id == null) {
            this.id = IdWorker.nextId();
        }

        var now = LocalDateTime.now();
        var session = session();

        if (session.getRootId() == null || session.getDeptId() == null) {
            throw new AuthException("用户不属于任何租户或部门,无法创建条目!");
        }

        if (this.createTime == null) {
            this.createTime = now;
        }
        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }

        if (this.creatorId == null) {
            this.creatorId = session.getUserId();
        }
        if (this.updaterId == null) {
            this.updaterId = session.getUserId();
        }
        if (this.rootId == null) {
            this.rootId = session.getRootId();
        }
        if (this.deptId == null) {
            this.deptId = session.getDeptId();
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
