package com.ksptooi.biz.qt.model.qttask;

import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

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

    @Column(name = "target", length = 1000, comment = "调用目标(BEAN代码或HTTP地址)")
    private String target;

    @Column(name = "target_param", columnDefinition = "json", comment = "调用参数JSON")
    private String targetParam;

    @Column(name = "req_method", length = 32, comment = "请求方法")
    private String reqMethod;

    @Column(name = "concurrent", nullable = false, columnDefinition = "TINYINT", comment = "并发执行 0:允许 1:禁止")
    private Integer concurrent;

    @Column(name = "misfire_policy", nullable = false, columnDefinition = "TINYINT", comment = "过期策略 0:放弃执行 1:立即执行 2:全部执行")
    private Integer misfirePolicy;

    @Column(name = "expire_time", comment = "任务有效期截止")
    private LocalDateTime expireTime;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT", comment = "0:正常 1:暂停")
    private Integer status;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @Column(name = "update_time", nullable = false, comment = "更新时间")
    private LocalDateTime updateTime;

    @Column(name = "updator_id", nullable = false, comment = "更新人ID")
    private Long updatorId;

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

        if (this.updateTime == null) {
            this.updateTime = this.createTime;
        }

        if (this.creatorId == null) {
            this.creatorId = session().getUserId();
        }

    }

    @PreUpdate
    private void onUpdate() {

        this.updateTime = LocalDateTime.now();
    }
}
