package com.ksptool.bio.biz.core.model.noticercd;

import com.ksptool.bio.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.LastModifiedBy;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;


@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "core_notice_rcd")
@SQLDelete(sql = "UPDATE core_notice_rcd SET delete_time = NOW() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class NoticeRcdPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "notice_id", nullable = false, comment = "关联通知ID")
    private Long noticeId;

    @Column(name = "user_id", nullable = false, comment = "接收人用户ID")
    private Long userId;

    @Column(name = "read_time", comment = "读取时间 (NULL代表未读)")
    private LocalDateTime readTime;

    @CreatedDate
    @Column(name = "create_time", nullable = false, comment = "下发时间")
    private LocalDateTime createTime;

    @Column(name = "delete_time", nullable = true, comment = "删除时间 (NULL代表未删)")
    private LocalDateTime deleteTime;


    @PrePersist
    private void onCreate() {


    }

    @PreUpdate
    private void onUpdate() {


    }
}
