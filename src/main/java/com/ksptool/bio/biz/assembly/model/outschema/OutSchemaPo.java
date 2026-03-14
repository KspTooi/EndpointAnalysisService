package com.ksptool.bio.biz.assembly.model.outschema;

import com.ksptool.assembly.entity.exception.AuthException;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Setter
@Entity
@Table(name = "gen_out_schema")
@EntityListeners(AuditingEntityListener.class)
public class OutSchemaPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "data_source_id", comment = "数据源ID")
    private Long dataSourceId;

    @Column(name = "type_schema_id", comment = "类型映射方案ID")
    private Long typeSchemaId;

    @Column(name = "input_scm_id", comment = "输入SCM ID")
    private Long inputScmId;

    @Column(name = "output_scm_id", comment = "输出SCM ID")
    private Long outputScmId;

    @Column(name = "name", nullable = false, length = 32, comment = "输出方案名称")
    private String name;

    @Column(name = "model_name", nullable = false, length = 255, comment = "模型名称")
    private String modelName;

    @Column(name = "table_name", length = 80, comment = "数据源表名")
    private String tableName;

    @Column(name = "remove_table_prefix", length = 80, comment = "移除表前缀")
    private String removeTablePrefix;

    @Column(name = "perm_code_prefix", nullable = false, length = 32, comment = "权限码前缀")
    private String permCodePrefix;

    @Column(name = "policy_override", nullable = false, columnDefinition = "TINYINT", comment = "写出策略 0:不覆盖 1:覆盖")
    private Integer policyOverride;

    @Column(name = "base_input", nullable = false, length = 320, comment = "输入基准路径")
    private String baseInput;

    @Column(name = "base_output", nullable = false, length = 320, comment = "输出基准路径")
    private String baseOutput;

    @Column(name = "remark", columnDefinition = "TEXT", comment = "备注")
    private String remark;

    @Column(name = "field_count_origin", nullable = false, comment = "字段数(原始)")
    private Integer fieldCountOrigin;

    @Column(name = "field_count_poly", nullable = false, comment = "字段数(聚合)")
    private Integer fieldCountPoly;

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
