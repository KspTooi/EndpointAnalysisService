package com.ksptooi.biz.drive.model.po;


import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import java.time.LocalDateTime;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;


@Getter
@Setter
@Entity
@Table(name = "drive_entry")
@SQLDelete(sql = "UPDATE drive_entry SET delete_time = now() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class EntryPo {

    @Column(name = "id")
    @Id
    @Comment("条目ID")
    private Long id;

    @Column(name = "company_id",nullable = false)
    @Comment("团队ID")
    private Long companyId;

    @Column(name = "parent_id")
    @Comment("父级ID 为NULL顶级")
    private Long parentId;

    @Column(name = "name",length = 128,nullable = false)
    @Comment("条目名称")
    private String name;

    @Column(name = "kind")
    @Comment("条目类型 0:文件 1:文件夹")
    private Integer kind;

    @Column(name = "attach_id")
    @Comment("文件附件ID 文件夹为NULL")
    private Long attachId;

    @Column(name = "attach_size")
    @Comment("文件附件大小 文件夹为0")
    private Long attachSize;

    @Column(name = "attach_suffix",length = 128)
    @Comment("文件附件类型 文件夹为NULL")
    private String attachSuffix;

    @Column(name = "create_time",nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time",nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "delete_time")
    @Comment("删除时间 为NULL未删除")
    private LocalDateTime deleteTime;

    @Column(name = "creator_id",nullable = false)
    @Comment("创建人")
    private Long creatorId;

    @Column(name = "updater_id",nullable = false)
    @Comment("更新人")
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
