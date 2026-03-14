package com.ksptool.bio.biz.auth.model;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Setter
@IdClass(GroupDeptPo.Pk.class)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "auth_group_dept", comment = "GD表")
public class GroupDeptPo {

    @Id
    @Column(name = "group_id", nullable = false, comment = "组ID")
    private Long groupId;

    @Id
    @Column(name = "dept_id", nullable = false, comment = "部ID")
    private Long deptId;

    @CreatedDate
    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @PrePersist
    private void onCreate() {

    }

    @PreUpdate
    private void onUpdate() {

    }

    /**
     * 用于复合主键的类
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Pk implements Serializable {
        private Long groupId;
        private Long deptId;
    }
}
