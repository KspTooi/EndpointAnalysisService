package com.ksptool.bio.biz.outschema.model;

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
@Table(name = "out_schema")
public class OutSchemaPo {

    @Column(name = "id", comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "data_source_id", nullable = true, comment = "数据源ID")
    private Long dataSourceId;

    @Column(name = "type_schema_id", nullable = true, comment = "类型映射方案ID")
    private Long typeSchemaId;

    @Column(name = "input_scm_id", nullable = true, comment = "输入SCM ID")
    private Long inputScmId;

    @Column(name = "output_scm_id", nullable = true, comment = "输出SCM ID")
    private Long outputScmId;

    @Column(name = "name", comment = "输出方案名称")
    private String name;

    @Column(name = "model_name", comment = "模型名称")
    private String modelName;

    @Column(name = "table_name", nullable = true, comment = "数据源表名")
    private String tableName;

    @Column(name = "remove_table_prefix", comment = "移除表前缀")
    private String removeTablePrefix;

    @Column(name = "perm_code_prefix", comment = "权限码前缀")
    private String permCodePrefix;

    @Column(name = "policy_override", comment = "写出策略 0:不覆盖 1:覆盖")
    private Integer policyOverride;

    @Column(name = "base_input", comment = "输入基准路径")
    private String baseInput;

    @Column(name = "base_output", comment = "输出基准路径")
    private String baseOutput;

    @Column(name = "remark", nullable = true, comment = "备注")
    private String remark;

    @Column(name = "field_count_origin", comment = "字段数(原始)")
    private Integer fieldCountOrigin;

    @Column(name = "field_count_poly", comment = "字段数(聚合)")
    private Integer fieldCountPoly;

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
