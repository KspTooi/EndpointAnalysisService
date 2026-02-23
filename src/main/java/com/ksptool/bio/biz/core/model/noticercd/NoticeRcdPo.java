package com.ksptool.bio.biz.core.model.noticercd;

import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "core_notice_rcd")
@SQLDelete(sql = "UPDATE core_notice_rcd SET delete_time = NOW() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class NoticeRcdPo {

    @Column(name = "id", nullable = false, comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "notice_id", nullable = false, comment = "关联通知ID")
    private Long noticeId;

    @Column(name = "user_id", nullable = false, comment = "接收人用户ID")
    private Long userId;

    @Column(name = "read_time", comment = "读取时间 (NULL代表未读)")
    private LocalDateTime readTime;

    @Column(name = "create_time", nullable = false, comment = "下发时间")
    private LocalDateTime createTime;

    @Column(name = "delete_time", nullable = true, comment = "删除时间 (NULL代表未删)")
    private LocalDateTime deleteTime;


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
