package com.ksptool.bio.biz.gen.model.outblueprint;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.auth.service.SessionService;
import com.ksptool.bio.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "gen_out_blueprint")
public class GenOutBlueprintPo {

    @Column(name = "id", nullable = false, comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "name", nullable = false, length = 32, comment = "蓝图名称")
    private String name;

    @Column(name = "project_name", length = 80, comment = "项目名称")
    private String projectName;

    @Column(name = "code", nullable = false, length = 32, comment = "蓝图编码")
    private String code;

    @Column(name = "scm_url", nullable = false, length = 1000, comment = "SCM仓库地址")
    private String scmUrl;

    @Column(name = "scm_auth_kind", nullable = false, columnDefinition = "TINYINT", comment = "SCM认证方式 0:公开 1:账号密码 2:SSH KEY")
    private Integer scmAuthKind;

    @Column(name = "scm_username", columnDefinition = "TEXT", comment = "SCM用户名")
    private String scmUsername;

    @Column(name = "scm_password", columnDefinition = "TEXT", comment = "SCM密码")
    private String scmPassword;

    @Column(name = "scm_pk", columnDefinition = "TEXT", comment = "SSH KEY")
    private String scmPk;

    @Column(name = "scm_branch", nullable = false, length = 80, comment = "SCM分支")
    private String scmBranch;

    @Column(name = "scm_base_path", nullable = false, length = 1280, comment = "基准路径")
    private String scmBasePath;

    @Column(name = "remark", nullable = false, columnDefinition = "TEXT", comment = "蓝图备注")
    private String remark;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "更新人ID")
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
