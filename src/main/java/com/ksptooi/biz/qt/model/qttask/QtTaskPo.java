package com.ksptooi.biz.qt.model.qttask;

import com.ksptooi.biz.qt.common.QuickTaskRegistry;
import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.quartz.CronExpression;

import java.time.LocalDateTime;

import static com.ksptooi.biz.core.service.SessionService.session;

@Getter
@Setter
@Entity
@Table(name = "qt_task")
@SQLRestriction("delete_time IS NULL")
@SQLDelete(sql = "UPDATE qt_task SET delete_time = NOW() WHERE id = ?")
public class QtTaskPo {

    @Column(name = "id", nullable = false, comment = "任务ID")
    @Id
    private Long id;

    @Column(name = "group_id", comment = "任务分组ID")
    private Long groupId;

    @Column(name = "group_name", length = 80, comment = "任务分组名")
    private String groupName;

    @Column(name = "name", nullable = false, length = 80, comment = "任务名")
    private String name;

    @Column(name = "kind", nullable = false, columnDefinition = "TINYINT", comment = "0:本地BEAN 1:远程HTTP")
    private Integer kind;

    @Column(name = "cron", nullable = false, length = 64, comment = "CRON表达式")
    private String cron;

    @Column(name = "target", nullable = false, length = 1000, comment = "调用目标(BEAN代码或HTTP地址)")
    private String target;

    @Column(name = "target_param", columnDefinition = "json", comment = "调用参数JSON")
    private String targetParam;

    @Column(name = "req_method", length = 32, comment = "请求方法")
    private String reqMethod;

    @Column(name = "concurrent", nullable = false, columnDefinition = "TINYINT", comment = "并发执行 0:允许 1:禁止")
    private Integer concurrent;

    @Column(name = "policy_misfire", nullable = false, columnDefinition = "TINYINT", comment = "过期策略 0:放弃执行 1:立即执行 2:全部执行")
    private Integer policyMisfire;

    @Column(name = "policy_error", nullable = false, columnDefinition = "TINYINT", comment = "失败策略 0:默认 1:自动暂停")
    private Integer policyError;

    @Column(name = "policy_rcd", nullable = false, columnDefinition = "TINYINT", comment = "日志策略 0:全部 1:仅异常 2:不记录")
    private Integer policyRcd;

    @Column(name = "expire_time", comment = "任务有效期截止")
    private LocalDateTime expireTime;

    @Column(name = "last_exec_status", columnDefinition = "TINYINT", comment = "上次状态 0:成功 1:异常")
    private Integer lastExecStatus;

    @Column(name = "last_start_time", comment = "上次开始时间")
    private LocalDateTime lastStartTime;

    @Column(name = "last_end_time", comment = "上次结束时间")
    private LocalDateTime lastEndTime;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT", comment = "0:正常 1:暂停 2:暂停(异常)")
    private Integer status;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updater_id", nullable = false, comment = "更新人ID")
    private Long updaterId;

    @Column(name = "delete_time", comment = "删除时间 NULL未删")
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

        if (this.creatorId == null) {
            this.creatorId = session().getUserId();
        }

        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }

        if (this.updaterId == null) {
            this.updaterId = session().getUserId();
        }

    }

    @PreUpdate
    private void onUpdate() {
        this.updateTime = LocalDateTime.now();
    }

    /**
     * 获取任务唯一标识
     * @return 任务唯一标识
     */
    public String getIdentity() {
        return "TASK_" + this.getId();
    }

    public String validate(){

        //检查调用目标(本地BEAN时)
        if(this.kind == 0){
            if(!QuickTaskRegistry.contains(this.target)){
                return "本地BEAN不存在: " + this.target;
            }
        }

        //检查Cron合法性
        if(!CronExpression.isValidExpression(this.cron)){
            return "CRON表达式不合法: " + this.cron;
        }

        return null;
    }

}
