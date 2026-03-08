package com.ksptool.bio.biz.gen.model.outmodelpoly;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.core.common.jpa.SetStringConv;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "gen_out_model_poly")
@EntityListeners(AuditingEntityListener.class)
public class OutModelPolyPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "output_schema_id", nullable = false, comment = "输出方案ID")
    private Long outputSchemaId;

    @Column(name = "output_model_origin_id", nullable = false, comment = "原始字段ID")
    private Long outputModelOriginId;

    @Column(name = "name", nullable = false, length = 255, comment = "聚合字段名")
    private String name;

    @Column(name = "kind", nullable = false, length = 255, comment = "聚合数据类型")
    private String kind;

    @Column(name = "length", length = 255, comment = "聚合长度")
    private String length;

    @Column(name = "`require`", nullable = false, columnDefinition = "TINYINT", comment = "聚合必填 0:否 1:是")
    private Integer require;

    @Column(name = "policy_crud_json", nullable = false, columnDefinition = "JSON", comment = "聚合可见性策略 ADD、EDIT、LQ、LW")
    @Convert(converter = SetStringConv.class)
    private Set<String> policyCrudJson;

    @Column(name = "policy_query", nullable = false, columnDefinition = "TINYINT", comment = "聚合查询策略 0:等于 1:模糊")
    private Integer policyQuery;

    @Column(name = "policy_view", nullable = false, columnDefinition = "TINYINT", comment = "聚合显示策略 0:文本框 1:文本域 2:下拉 3:单 4:多 5:LD 6:LDT")
    private Integer policyView;

    @Column(name = "remark", nullable = false, length = 80, comment = "聚合字段备注")
    private String remark;

    @Column(name = "seq", nullable = false, comment = "聚合排序")
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
