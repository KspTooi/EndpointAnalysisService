package com.ksptool.bio.biz.qfmodeldeployrcd.model;

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
@Table(name = "qf_model_deploy_rcd")
public class QfModelDeployRcdPo {

    @Column(name = "id", comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "root_id", comment = "所属企业/租户ID")
    private Long rootId;

    @Column(name = "dept_id", comment = "所属部门ID")
    private Long deptId;

    @Column(name = "model_id", comment = "模型ID")
    private Long modelId;

    @Column(name = "name", comment = "模型名称")
    private String name;

    @Column(name = "code", comment = "模型编码")
    private String code;

    @Column(name = "bpmn_xml", nullable = true, comment = "模型BPMN XML")
    private String bpmnXml;

    @Column(name = "data_version", comment = "模型版本号")
    private Integer dataVersion;

    @Column(name = "eng_deployment_id", nullable = true, comment = "引擎"部署ID"(部署失败为NULL)")
    private String engDeploymentId;

    @Column(name = "eng_process_def_id", nullable = true, comment = "引擎"流程ID"(部署失败为NULL)")
    private String engProcessDefId;

    @Column(name = "eng_deploy_result", comment = "引擎返回的部署结果")
    private String engDeployResult;

    @Column(name = "status", comment = "部署状态 0:正常 1:部署失败")
    private Integer status;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", comment = "更新人ID")
    private Long updaterId;

    @Column(name = "delete_time", nullable = true, comment = "删除时间 NULL未删")
    private LocalDateTime deleteTime;


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
