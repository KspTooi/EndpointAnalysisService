package com.ksptool.bio.biz.assembly.model.outmodelorigin;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "assembly_out_model_origin")
@EntityListeners(AuditingEntityListener.class)
public class OutModelOriginPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "output_schema_id", nullable = false, comment = "输出方案ID")
    private Long outputSchemaId;

    @Column(name = "name", nullable = false, length = 255, comment = "原始字段名")
    private String name;

    @Column(name = "kind", nullable = false, length = 255, comment = "原始数据类型")
    private String kind;

    @Column(name = "length", length = 255, comment = "原始长度")
    private String length;

    @Column(name = "`require`", nullable = false, columnDefinition = "TINYINT", comment = "原始必填 0:否 1:是")
    private Integer require;

    @Column(name = "remark", length = 255, comment = "原始备注")
    private String remark;

    @Column(name = "seq", nullable = false, comment = "原始排序")
    private Integer seq;

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
