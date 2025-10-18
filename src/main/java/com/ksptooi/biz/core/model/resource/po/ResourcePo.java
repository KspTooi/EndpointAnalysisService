package com.ksptooi.biz.core.model.resource.po;

import com.ksptooi.biz.user.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

/**
 * 资源
 *
 * @author: Dean
 * @date: 2025年08月05日 10:00
 */
@Getter
@Setter
@Entity
@Table(name = "core_resource")
public class ResourcePo {

    @Column(name = "id")
    @Id
    @Comment("主键id")
    private Long id;

    @Comment("父级ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT))
    private ResourcePo parent;

    @Column(name = "name", nullable = false, length = 128)
    @Comment("资源名")
    private String name;

    @Column(name = "description")
    @Comment("资源描述")
    private String description;

    @Column(name = "kind", nullable = false)
    @Comment("资源类型 0:菜单 1:接口")
    private Integer kind;

    @Column(name = "menu_kind")
    @Comment("菜单类型 0:目录 1:菜单 2:按钮")
    private Integer menuKind;

    @Column(name = "menu_path", length = 256)
    @Comment("菜单路径")
    private String menuPath;

    @Column(name = "menu_query_param")
    @Comment("菜单查询参数")
    private String menuQueryParam;

    @Column(name = "menu_icon")
    @Comment("菜单图标")
    private String menuIcon;

    @Column(name = "path", length = 256)
    @Comment("接口路径")
    private String path;

    @Column(name = "permission", nullable = false, length = 320)
    @Comment("所需权限")
    private String permission;

    @Column(name = "seq", nullable = false)
    @Comment("排序")
    private Integer seq;

    @Column(name = "create_time")
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id")
    @Comment("创建人ID")
    private Long creatorId;

    @Column(name = "update_time")
    @Comment("更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id")
    @Comment("更新人ID")
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
