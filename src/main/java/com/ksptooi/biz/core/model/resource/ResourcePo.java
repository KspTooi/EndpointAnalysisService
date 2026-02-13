package com.ksptooi.biz.core.model.resource;

import com.ksptooi.commons.dataprocess.Str;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ksptooi.biz.auth.service.SessionService.session;

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

    @Column(name = "menu_kind", columnDefinition = "tinyint", comment = "菜单类型 0:目录 1:菜单 2:按钮(kind = 0时生效)")
    private Integer menuKind;

    @Column(name = "menu_path", length = 256, comment = "菜单路径(menuKind = 1时生效)")
    private String menuPath;

    @Column(name = "menu_query_param", length = 512, comment = "菜单查询参数(menuKind = 1时生效)")
    private String menuQueryParam;

    @Column(name = "menu_icon", length = 64, comment = "菜单图标(menuKind = 1时生效)")
    private String menuIcon;

    @Column(name = "menu_hidden", columnDefinition = "tinyint", comment = "是否隐藏 0:否 1:是(menuKind = 1/2时生效)")
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


    /**
     * 检查当前资源是否拥有指定权限集合中的任意一个权限
     *
     * @param permissions 权限集合
     * @return 如果资源拥有任意一个权限返回true，否则返回false
     */
    public boolean hasPermission(List<GrantedAuthority> permissions) {

        if (permissions == null || permissions.isEmpty()) {
            return false;
        }

        for (var permission : permissions) {
            if (this.hasPermission(permission.toString())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 检查当前资源是否拥有指定权限
     * <p>
     * * 为通配符权限，表示所有用户均可访问
     * 资源中的permission字段是多个权限以;分隔，所以需要检查多个权限是否都拥有
     *
     * @param permission 权限码，如：system:user:view
     * @return 如果资源拥有该权限返回true，否则返回false
     */
    public boolean hasPermission(String permission) {

        //如果权限为空或*，则表示所有用户均可访问
        if (StringUtils.isBlank(this.permission) || this.permission.equals("*")) {
            return true;
        }

        //资源所需的权限列表
        var requireCodes = new ArrayList<String>();

        //解析资源权限
        if (this.permission.contains(";")) {
            requireCodes.addAll(Str.safeSplit(this.permission, ";"));
        }
        if (!this.permission.contains(";")) {
            requireCodes.add(this.permission);
        }

        //检查给定的权限是否在资源所需的权限列表中
        for (var requireCode : requireCodes) {
            if (requireCode.equals(permission)) {
                return true;
            }
        }

        return false;
    }


}
