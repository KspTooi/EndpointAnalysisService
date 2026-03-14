package com.ksptool.bio.biz.core.model.exceltemplate;


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

/**
 * ${tableComment}
 *
 * @author: generator
 * @date: ${.now?string("yyyy年MM月dd日 HH:mm")}
 */
@Getter
@Setter
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "core_excel_template", indexes = {
        @Index(name = "uk_code_delete_time", columnList = "code,delete_time", unique = true)
})
@SQLDelete(sql = "UPDATE core_excel_template SET delete_time = now() WHERE id = ?")
@SQLRestriction("delete_time IS NULL")
public class ExcelTemplatePo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "模板ID")
    private Long id;

    @Column(name = "attach_id", nullable = false, comment = "模板附件ID")
    private Long attachId;

    @Column(name = "name", nullable = false, length = 32, comment = "模板名称")
    private String name;

    @Column(name = "code", nullable = false, length = 32, comment = "模板标识 唯一")
    private String code;

    @Column(name = "remark", columnDefinition = "text", comment = "模板备注")
    private String remark;

    @Column(name = "status", nullable = false, columnDefinition = "tinyint", comment = "状态 0:启用 1:禁用")
    private Integer status;

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

    @Column(name = "delete_time", comment = "删除时间 NULL未删除")
    private LocalDateTime deleteTime;

    @PrePersist
    private void onCreate() throws AuthException {

    }

    @PreUpdate
    private void onUpdate() throws AuthException {

    }


}
