package com.ksptool.bio.biz.auth.model.permission;

import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import com.ksptool.bio.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.auth.service.SessionService;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.LastModifiedBy;

/**
 * 权限实体类
 * 用于管理系统中的权限节点信息
 * 采用平级设计，权限标识使用 : 分隔表示层级
 * 例如：system:user:view, system:user:edit
 */

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "auth_permission", comment = "权限码表")
public class PermissionPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", comment = "权限ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 32, comment = "权限名称，如：查看用户")
    private String name;

    @Column(name = "code", nullable = false, unique = true, length = 320, comment = "权限标识，如：system:user:view")
    private String code;

    @Column(name = "remark", columnDefinition = "TEXT", comment = "权限描述")
    private String remark;

    @Column(name = "seq", nullable = false, comment = "排序号")
    private Integer seq;

    @Column(name = "is_system", nullable = false, columnDefinition = "TINYINT", comment = "系统内置权限 0:否 1:是")
    private Integer isSystem = 0;

    @CreatedDate
    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @CreatedBy
    @Column(name = "creator_id", nullable = false, updatable = false, comment = "创建人")
    private Long creatorId;

    @LastModifiedDate
    @Column(name = "update_time", nullable = false, comment = "修改时间")
    private LocalDateTime updateTime;

    @LastModifiedBy
    @Column(name = "updater_id", nullable = false, comment = "修改人")
    private Long updaterId;

    @PrePersist
    private void onCreate() throws AuthException {


        if (seq == null) {
            seq = 0;
        }



    }

    @PreUpdate
    private void onUpdate() throws AuthException {


    }

    /**
     * 判断两个权限是否相等 只要权限码相等就认为相等
     *
     * @param obj 要比较的权限
     * @return 是否相等
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PermissionPo other = (PermissionPo) obj;
        return Objects.equals(this.code, other.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
} 