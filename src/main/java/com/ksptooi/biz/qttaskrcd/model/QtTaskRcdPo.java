package com.ksptooi.biz.qttaskrcd.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.ksptooi.biz.core.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import java.time.LocalDateTime;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "qt_task_rcd")
public class QtTaskRcdPo {

    @Column(name = "id", comment = "调度日志ID")
    @Id
    private Long id;

    @Column(name = "task_id", comment = "任务ID")
    private Long taskId;

    @Column(name = "task_name", comment = "任务名")
    private String taskName;

    @Column(name = "group_name", nullable = true, comment = "分组名")
    private String groupName;

    @Column(name = "target", comment = "调用目标")
    private String target;

    @Column(name = "target_param", nullable = true, comment = "调用目标参数")
    private String targetParam;

    @Column(name = "target_result", nullable = true, comment = "调用目标返回内容(错误时为异常堆栈)")
    private String targetResult;

    @Column(name = "status", comment = "运行状态 0:正常 1:失败 2:超时 3:已调度")
    private Integer status;

    @Column(name = "start_time", comment = "运行开始时间")
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = true, comment = "运行结束时间")
    private LocalDateTime endTime;

    @Column(name = "cost_time", nullable = true, comment = "耗时(MS)")
    private Integer costTime;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;


    @PrePersist
    private void onCreate() {

        if (this.id == null) {
            this.id = IdWorker.nextId();
        }
        
        
        LocalDateTime now = LocalDateTime.now();
        
        if (this.createTime == null) {
            this.createTime = now;
        }
        
        
        
    }

    @PreUpdate
    private void onUpdate() {
        
        
    }
}
