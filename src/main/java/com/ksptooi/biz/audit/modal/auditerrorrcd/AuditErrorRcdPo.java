package com.ksptooi.biz.audit.modal.auditerrorrcd;

import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "audit_error_rcd")
public class AuditErrorRcdPo {

    @Column(name = "id", comment = "错误ID")
    @Id
    private Long id;

    @Column(name = "error_code", nullable = false, length = 32, comment = "错误代码")
    private String errorCode;

    @Column(name = "request_uri", nullable = false, comment = "请求地址")
    private String requestUri;

    @Column(name = "user_id", nullable = true, comment = "操作人ID")
    private Long userId;

    @Column(name = "user_name", nullable = true, comment = "操作人用户名")
    private String userName;

    @Column(name = "error_type", nullable = false, comment = "异常类型")
    private String errorType;

    @Column(name = "error_message", nullable = false, comment = "异常简述")
    private String errorMessage;

    @Column(name = "error_stack_trace", nullable = true, comment = "完整堆栈信息")
    private String errorStackTrace;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
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
