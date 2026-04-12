package com.ksptool.bio.biz.core.model.menu;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.auth.service.SessionService;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import com.ksptool.bio.commons.dataprocess.Str;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "core_menu")
@EntityListeners(AuditingEntityListener.class)
@SQLDelete(sql = "UPDATE core_menu SET delete_time = NOW() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class MenuPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "root_id", nullable = false, comment = "租户ID")
    private Long rootId;

    @Column(name = "dept_id", nullable = false, comment = "部门ID")
    private Long deptId;

    @Column(name = "parent_id", comment = "父级项ID")
    private Long parentId;

    @Column(name = "name", nullable = false, length = 32, comment = "菜单项名")
    private String name;

    @Column(name = "kind", nullable = false, comment = "菜单项类型 0:目录 1:菜单 2:按钮")
    private Integer kind;

    @Column(name = "path", length = 500, comment = "指向路径")
    private String path;

    @Column(name = "icon", nullable = false, length = 80, comment = "菜单图标")
    private String icon;

    @Column(name = "hide", nullable = false, comment = "隐藏 0:否 1:是")
    private Integer hide;

    @Column(name = "permission_code", length = 500, comment = "所需权限码(多个逗号)")
    private String permissionCode;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "remark", comment = "备注")
    private String remark;

    @CreatedDate
    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @CreatedBy
    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @LastModifiedDate
    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @LastModifiedBy
    @Column(name = "updater_id", nullable = false, comment = "更新人ID")
    private Long updaterId;

    @Column(name = "delete_time", comment = "删除时间")
    private LocalDateTime deleteTime;


    @PrePersist
    private void onCreate() throws AuthException {
        var session = SessionService.session();
        if (this.rootId == null) {
            this.rootId = session.getRootId();
        }
        if (this.deptId == null) {
            this.deptId = session.getDeptId();
        }
    }

    @PreUpdate
    private void onUpdate() throws AuthException {

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
     * 资源中的permissionCode字段是多个权限以;分隔，所以需要检查多个权限是否都拥有
     * 权限为空时表示所有用户均可访问
     *
     * @param permission 权限码，如：system:user:view
     * @return 如果资源拥有该权限返回true，否则返回false
     */
    public boolean hasPermission(String permission) {

        //如果给定的权限是超级权限 直接放行
        if (StringUtils.isNotBlank(permission) && permission.equals("*:*:*")) {
            return true;
        }

        //如果权限为空，则表示所有用户均可访问
        if (StringUtils.isBlank(this.permissionCode)) {
            return true;
        }

        //资源所需的权限列表
        var requireCodes = new ArrayList<String>();

        //解析资源权限
        if (this.permissionCode.contains(";")) {
            requireCodes.addAll(Str.safeSplit(this.permissionCode, ";"));
        }
        if (!this.permissionCode.contains(";")) {
            requireCodes.add(this.permissionCode);
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
