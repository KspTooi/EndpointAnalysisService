package com.ksptooi.biz.user.model.resources;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

@Entity
@Table(name = "resource")
@Getter
@Setter
@Comment("资源表")
public class ResourcePo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("资源ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("父级资源ID")
    private ResourcePo parent;

    @Column(name = "name", length = 128, nullable = false)
    @Comment("资源名称")
    private String name;

    @Column(name = "description", length = 128, nullable = false)
    @Comment("资源描述")
    private String description;

    @Column(name = "kind", nullable = false, columnDefinition = "tinyint")
    @Comment("资源类型 0:菜单 1:接口")
    private Integer kind;

    @Column(name = "menu_kind", nullable = false, columnDefinition = "tinyint")
    @Comment("菜单类型 0:目录 1:菜单 2:按钮")
    private Integer menuKind;

    @Column(name = "menu_path", length = 128, nullable = false)
    @Comment("菜单路径")
    private String menuPath;

    @Column(name = "menu_query_param", length = 128, nullable = false)
    @Comment("菜单查询参数")
    private String menuQueryParam;

    @Column(name = "menu_icon", length = 128, nullable = false)
    @Comment("菜单图标")
    private String menuIcon;

    @Column(name = "path", nullable = false, columnDefinition = "int")
    @Comment("接口路径")
    private Integer path;

    @Column(name = "permission", length = 128, nullable = false)
    @Comment("所需权限")
    private String permission;

    @Column(name = "seq", nullable = false)
    @Comment("排序")
    private Integer seq;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    @Comment("更新时间")
    private LocalDateTime updateTime;
    

    @PrePersist
    public void prePersist() {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updateTime = LocalDateTime.now();
    }

}
