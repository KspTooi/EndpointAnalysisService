package com.ksptool.bio.biz.qt.model.qttaskgroup;

import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.LastModifiedBy;

@Getter
@Setter
@Entity 
@EntityListeners(AuditingEntityListener.class)
@Table(name = "qt_task_group")
public class QtTaskGroupPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 80, comment = "分组名")
    private String name;

    @Column(name = "remark", length = 1000, comment = "分组备注")
    private String remark;

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

    @Column(name = "delete_time", comment = "删除时间 NULL未删")
    private LocalDateTime deleteTime;


    @PrePersist
    private void onCreate() throws AuthException {



    }

    @PreUpdate
    private void onUpdate() throws AuthException {

    }
}
