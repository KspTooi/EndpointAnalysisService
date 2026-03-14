package com.ksptool.bio.biz.qt.model.qttaskrcd;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "qt_task_rcd")
public class QtTaskRcdPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "调度日志ID")
    private Long id;

    @Column(name = "task_id", nullable = false, comment = "任务ID")
    private Long taskId;

    @Column(name = "task_name", nullable = false, length = 80, comment = "任务名")
    private String taskName;

    @Column(name = "group_name", length = 80, comment = "分组名")
    private String groupName;

    @Column(name = "target", nullable = false, length = 1000, comment = "调用目标")
    private String target;

    @Column(name = "target_param", columnDefinition = "json", comment = "调用目标参数")
    private String targetParam;

    @Column(name = "target_result", columnDefinition = "longtext", comment = "调用目标返回内容(错误时为异常堆栈)")
    private String targetResult;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint", comment = "运行状态 0:正常 1:失败 2:超时 3:已调度")
    private Integer status;

    @Column(name = "start_time", nullable = false, comment = "运行开始时间")
    private LocalDateTime startTime;

    @Column(name = "end_time", comment = "运行结束时间")
    private LocalDateTime endTime;

    @Column(name = "cost_time", comment = "耗时(MS)")
    private Integer costTime;

    @CreatedDate
    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;


    @PrePersist
    private void onCreate() {


    }

    @PreUpdate
    private void onUpdate() {


    }
}
