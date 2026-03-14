package com.ksptool.bio.biz.core.model.post;

import com.ksptool.bio.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import static com.ksptool.bio.biz.auth.service.SessionService.session;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.LastModifiedBy;

@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "core_post")
@SQLRestriction("delete_time IS NULL")
@SQLDelete(sql = "UPDATE core_post SET delete_time = NOW() WHERE id = ?")
public class PostPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "岗位ID")
    private Long id;

    @Column(name = "name", length = 32, nullable = false, comment = "岗位名称")
    private String name;

    @Column(name = "code", length = 32, nullable = false, comment = "岗位编码")
    private String code;

    @Column(name = "seq", nullable = false, comment = "岗位排序")
    private Integer seq;

    @Column(name = "status", nullable = false, columnDefinition = "TINYINT", comment = "0:启用 1:停用")
    private Integer status;

    @Column(name = "remark", columnDefinition = "TEXT", comment = "备注")
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
