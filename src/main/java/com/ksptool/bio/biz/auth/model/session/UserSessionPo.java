package com.ksptool.bio.biz.auth.model.session;

import com.ksptool.bio.biz.auth.common.aop.RowScopePo;
import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptool.bio.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import static com.ksptool.entities.Entities.toJson;

@Entity
@Table(name = "auth_user_session", comment = "用户会话")
@Getter
@Setter
public class UserSessionPo extends RowScopePo {

    @Id
    @Column(name = "id", comment = "会话ID")
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true, length = 128, comment = "用户凭据SessionID")
    private String sessionId;

    @Column(name = "user_id", nullable = false, comment = "用户ID")
    private Long userId;

    @Column(name = "root_id", comment = "所属企业ID")
    private Long rootId;

    @Column(name = "root_name", length = 32, comment = "所属企业名")
    private String rootName;

    @Column(name = "dept_id", comment = "所属部门ID")
    private Long deptId;

    @Column(name = "dept_name", length = 32, comment = "所属部门名")
    private String deptName;

    @Column(name = "company_id", comment = "公司ID")
    private Long companyId;

    @Column(name = "permissions", nullable = false, columnDefinition = "JSON", comment = "用户权限代码JSON")
    private String permissionCodes;

    @Column(name = "rs_max", nullable = false, columnDefinition = "TINYINT", comment = "最大RowScope等级 0:全部 1:本公司/租户及以下 2:本部门及以下 3:本部门 4:仅本人 5:指定部门")
    private Integer rsMax;

    @Column(name = "rs_allow_depts", nullable = false, columnDefinition = "JSON", comment = "RowScope允许访问的部门IDS")
    private String rsAllowDepts;

    @Column(name = "expires_at", nullable = false, comment = "过期时间")
    private LocalDateTime expiresAt;

    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, updatable = false, comment = "创建者ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "修改时间")
    private LocalDateTime updateTime;

    /**
     * 创建用户会话
     *
     * @param userPo           用户
     * @param permissionCodes  用户权限代码集合
     * @param expiresInSeconds 会话过期时间（秒）
     * @return 用户会话
     */
    public static UserSessionPo create(UserPo userPo, Set<String> permissionCodes, long expiresInSeconds) {
        var session = new UserSessionPo();
        session.setSessionId(UUID.randomUUID().toString());
        session.setUserId(userPo.getId());
        session.setRootId(userPo.getRootId());
        session.setRootName(userPo.getRootName());
        session.setDeptId(userPo.getDeptId());
        session.setDeptName(userPo.getDeptName());
        session.setCompanyId(userPo.getActiveCompanyId());
        session.setPermissionCodes(toJson(permissionCodes));
        session.setExpiresAt(LocalDateTime.now().plusSeconds(expiresInSeconds));
        session.setCreatorId(userPo.getId());
        return session;
    }

    /**
     * 更新用户会话
     *
     * @param userPo           用户
     * @param permissionCodes  用户权限代码集合
     * @param expiresInSeconds 会话过期时间（秒）
     */
    public void update(UserPo userPo, Set<String> permissionCodes, long expiresInSeconds) {
        this.rootId = userPo.getRootId();
        this.rootName = userPo.getRootName();
        this.deptId = userPo.getDeptId();
        this.deptName = userPo.getDeptName();
        this.companyId = userPo.getActiveCompanyId();
        this.permissionCodes = toJson(permissionCodes);
        this.expiresAt = LocalDateTime.now().plusSeconds(expiresInSeconds);
    }


    @PrePersist
    public void prePersist() {

        if (this.id == null) {
            this.id = IdWorker.nextId();
        }

        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updateTime = LocalDateTime.now();
    }

    /**
     * 判断会话是否已过期
     *
     * @return 是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

}