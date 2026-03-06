package com.ksptool.bio.biz.gen.model.gendatsource;

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
@Table(name = "gen_dat_source")
public class GenDatSourcePo {

    @Column(name = "id", nullable = false, comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "name", nullable = false, length = 32, comment = "数据源名称")
    private String name;

    @Column(name = "code", nullable = false, length = 32, comment = "数据源编码")
    private String code;

    @Column(name = "kind", nullable = false, columnDefinition = "TINYINT", comment = "数据源类型 0:MYSQL")
    private Integer kind;

    @Column(name = "drive", nullable = false, length = 80, comment = "JDBC驱动")
    private String drive;

    @Column(name = "url", nullable = false, columnDefinition = "TEXT", comment = "连接字符串")
    private String url;

    @Column(name = "username", length = 320, comment = "连接用户名")
    private String username;

    @Column(name = "password", length = 1280, comment = "连接密码")
    private String password;

    @Column(name = "db_schema", nullable = false, length = 80, comment = "默认模式")
    private String dbSchema;

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
