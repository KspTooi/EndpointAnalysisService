package com.ksptool.bio.biz.assembly.model.datsource;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "assembly_data_source")
@EntityListeners(AuditingEntityListener.class)
public class DataSourcePo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "主键ID")
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


    @PrePersist
    private void onCreate() throws AuthException {


    }

    @PreUpdate
    private void onUpdate() throws AuthException {

    }
}
