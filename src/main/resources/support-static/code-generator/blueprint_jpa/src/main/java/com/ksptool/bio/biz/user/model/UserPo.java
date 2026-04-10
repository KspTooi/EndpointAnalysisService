package com.ksptool.bio.biz.user.model;

import com.ksptool.assembly.entity.exception.AuthException;
import java.time.LocalDateTime;
import com.ksptool.bio.biz.auth.service.SessionService;
import com.ksptool.bio.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user")
public class UserPo {

    @Column(name = "id", comment = "用户ID")
    @Id
    private String id;

    @Column(name = "username", comment = "用户名")
    private String username;

    @Column(name = "password", comment = "密码")
    private String password;

    @Column(name = "nickname", nullable = true, comment = "昵称")
    private String nickname;

    @Column(name = "gender", nullable = true, comment = "性别 0:男 1:女 2:不愿透露")
    private String gender;

    @Column(name = "phone", nullable = true, comment = "手机号码")
    private String phone;

    @Column(name = "email", nullable = true, comment = "邮箱")
    private String email;

    @Column(name = "login_count", comment = "登录次数")
    private String loginCount;

    @Column(name = "status", comment = "用户状态 0:正常 1:封禁")
    private String status;

    @Column(name = "last_login_time", nullable = true, comment = "最后登录时间")
    private String lastLoginTime;

    @Column(name = "root_id", nullable = true, comment = "所属企业ID")
    private String rootId;

    @Column(name = "root_name", nullable = true, comment = "所属企业名称")
    private String rootName;

    @Column(name = "dept_id", nullable = true, comment = "部门ID")
    private String deptId;

    @Column(name = "dept_name", nullable = true, comment = "部门名称")
    private String deptName;

    @Column(name = "active_company_id", nullable = true, comment = "已激活的公司ID(兼容字段)")
    private String activeCompanyId;

    @Column(name = "active_env_id", nullable = true, comment = "已激活的环境ID(兼容字段)")
    private String activeEnvId;

    @Column(name = "avatar_attach_id", nullable = true, comment = "用户头像附件ID")
    private String avatarAttachId;

    @Column(name = "is_system", comment = "内置用户 0:否 1:是")
    private String isSystem;

    @Column(name = "data_version", comment = "数据版本号")
    private String dataVersion;

    @Column(name = "create_time", comment = "创建时间")
    private String createTime;

    @Column(name = "creator_id", comment = "创建人ID")
    private String creatorId;

    @Column(name = "update_time", comment = "修改时间")
    private String updateTime;

    @Column(name = "updater_id", comment = "更新人ID")
    private String updaterId;

    @Column(name = "delete_time", nullable = true, comment = "删除时间 为NULL未删")
    private String deleteTime;


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
            this.creatorId = SessionService.session().getUserId();
        }
        
        if (this.updaterId == null) {
            this.updaterId = SessionService.session().getUserId();
        }
    }

    @PreUpdate
    private void onUpdate() throws AuthException {
        
        this.updateTime = LocalDateTime.now();
        
        if (this.updaterId == null) {
            this.updaterId = SessionService.session().getUserId();
        }
    }
}
