package com.ksptool.bio.biz.assembly.model.tymschema;

import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "assembly_tym_schema", indexes = {
        @Index(name = "uk_code", columnList = "code", unique = true)
})
@EntityListeners(AuditingEntityListener.class)
public class TymSchemaPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "name", nullable = false, length = 32, comment = "方案名称")
    private String name;

    @Column(name = "code", nullable = false, length = 32, comment = "方案编码(唯一)")
    private String code;

    @Column(name = "type_count", nullable = false, comment = "类型数量")
    private Integer typeCount;

    @Column(name = "default_type", nullable = false, length = 80, comment = "默认类型")
    private String defaultType;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

    @Column(name = "remark", columnDefinition = "TEXT", comment = "备注")
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


    @PrePersist
    private void onCreate() throws AuthException {

    }

    @PreUpdate
    private void onUpdate() throws AuthException {

    }
}
