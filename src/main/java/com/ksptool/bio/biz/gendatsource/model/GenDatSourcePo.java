package com.ksptool.bio.biz.gendatsource.model;

import com.ksptool.assembly.entity.exception.AuthException;
import java.time.LocalDateTime;
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

    @Column(name = "id", comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "name", comment = "数据源名称")
    private String name;

    @Column(name = "code", comment = "数据源编码")
    private String code;

    @Column(name = "kind", comment = "数据源类型 0:MYSQL")
    private Integer kind;

    @Column(name = "drive", comment = "JDBC驱动")
    private String drive;

    @Column(name = "url", comment = "连接字符串")
    private String url;

    @Column(name = "username", nullable = true, comment = "连接用户名")
    private String username;

    @Column(name = "password", nullable = true, comment = "连接密码")
    private String password;

    @Column(name = "db_schema", comment = "默认模式")
    private String dbSchema;

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
