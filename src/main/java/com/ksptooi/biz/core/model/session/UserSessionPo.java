package com.ksptooi.biz.core.model.session;

import com.ksptooi.biz.core.model.user.UserPo;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static com.ksptool.entities.Entities.*;

@Entity
@Table(name = "core_user_session", comment = "用户会话")
@Data
public class UserSessionPo {

    @Id
    @Column(name = "id", comment = "会话ID")
    private Long id;

    @Column(name = "session_id", nullable = false, unique = true, length = 128, comment = "用户凭据SessionID")
    private String sessionId;

    @Column(name = "user_id", nullable = false, comment = "用户ID")
    private Long userId;

    @Column(name = "root_id", nullable = false, comment = "所属企业ID")
    private Long rootId;

    @Column(name = "root_name", nullable = false, length = 32, comment = "所属企业名")
    private String rootName;

    @Column(name = "dept_id", nullable = false, comment = "所属部门ID")
    private Long deptId;

    @Column(name = "dept_name", nullable = false, length = 255, comment = "所属部门名")
    private String deptName;

    @Column(name = "company_id", nullable = true, comment = "公司ID")
    private Long companyId;

    @Column(name = "permissions", nullable = false, columnDefinition = "JSON", comment = "用户权限代码JSON")
    private String permissionCodes;

    @Column(name = "expires_at", nullable = false, comment = "过期时间")
    private LocalDateTime expiresAt;

    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建时间")
    private LocalDateTime createTime;

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

    /**
     * 转换为VO
     *
     * @return 转换后的用户会话VO
     */
    public UserSessionVo toVo() {
        var vo = new UserSessionVo();

        //映射基本字段
        assign(this, vo);

        //反序列化权限代码集合
        vo.setPermissionCodes(new HashSet<>(fromJsonArray(this.getPermissionCodes(), String.class)));
        return vo;
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