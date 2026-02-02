package com.ksptooi.biz.core.model.resource.po;

import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
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

    @Column(name = "id", comment = "主键id")
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "none", value = ConstraintMode.NO_CONSTRAINT), comment = "父级ID")
    private ResourcePo parent;

    @Column(name = "name", nullable = false, length = 128, comment = "资源名")
    private String name;

    @Column(name = "description", length = 200, comment = "资源描述")
    private String description;

    @Column(name = "kind", nullable = false, comment = "资源类型 0:菜单 1:接口")
    private Integer kind;

    @Column(name = "menu_kind",columnDefinition = "tinyint", comment = "菜单类型 0:目录 1:菜单 2:按钮(kind = 0时生效)")
    private Integer menuKind;

    @Column(name = "menu_path", length = 256, comment = "菜单路径(menuKind = 1时生效)")
    private String menuPath;

    @Column(name = "menu_query_param", length = 512, comment = "菜单查询参数(menuKind = 1时生效)")
    private String menuQueryParam;

    @Column(name = "menu_icon", length = 64, comment = "菜单图标(menuKind = 1时生效)")
    private String menuIcon;

    @Column(name = "menu_hidden",columnDefinition = "tinyint", comment = "是否隐藏 0:否 1:是(menuKind = 1/2时生效)")
    private Integer menuHidden;

    @Column(name = "menu_btn_id", length = 64, comment = "按钮ID(menuKind = 2时必填)")
    private String menuBtnId;

    @Column(name = "path", length = 256, comment = "接口路径")
    private String path;

    @Column(name = "permission", nullable = false, length = 320, comment = "所需权限 为*时表示不配置权限(所有用户均可访问) 多个以;分隔")
    private String permission;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", comment = "更新人ID")
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
