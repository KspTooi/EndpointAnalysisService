package com.ksptool.bio.biz.core.model.notice;

import com.ksptool.bio.commons.utils.IdWorker;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "core_notice")
public class NoticePo {

    @Column(name = "id", nullable = false, comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "title", length = 32, nullable = false, comment = "标题")
    private String title;

    @Column(name = "kind", columnDefinition = "TINYINT", nullable = false, comment = "种类: 0公告, 1业务提醒, 2私信")
    private Integer kind;

    @Column(name = "content", columnDefinition = "LONGTEXT", nullable = true, comment = "通知内容")
    private String content;

    @Column(name = "priority", columnDefinition = "TINYINT", nullable = false, comment = "优先级: 0:低 1:中 2:高")
    private Integer priority;

    @Column(name = "category", length = 32, nullable = true, comment = "业务类型/分类")
    private String category;

    @Column(name = "sender_id", nullable = true, comment = "发送人ID (NULL为系统)")
    private Long senderId;

    @Column(name = "sender_name", length = 32, nullable = true, comment = "发送人姓名")
    private String senderName;

    @Column(name = "target_kind", columnDefinition = "TINYINT", nullable = false, comment = "接收人类型 0:全员 1:指定部门 2:指定用户")
    private Integer targetKind;

    @Column(name = "target_count", nullable = true, comment = "预计接收人数")
    private Integer targetCount;

    @Column(name = "forward", length = 320, nullable = true, comment = "跳转URL/路由地址")
    private String forward;

    @Column(name = "params", columnDefinition = "JSON", nullable = true, comment = "动态参数 (JSON格式)")
    private String params;

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
