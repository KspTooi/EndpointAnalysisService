package com.ksptooi.biz.authgroupdept.model;

import java.time.LocalDateTime;
import com.ksptooi.biz.auth.service.AuthService;
import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "auth_group_dept")
public class AuthGroupDeptPo {

    @Column(name = "group_id", comment = "组ID")
    @Id
    private Long groupId;

    @Column(name = "dept_id", comment = "部ID")
    @Id
    private Long deptId;

    @Column(name = "create_time", comment = "创建时间")
    private LocalDateTime createTime;


    @PrePersist
    private void onCreate() {

        if (this.groupId == null) {
            this.groupId = IdWorker.nextId();
        }
        if (this.deptId == null) {
            this.deptId = IdWorker.nextId();
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
