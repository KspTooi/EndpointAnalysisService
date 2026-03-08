package com.ksptool.bio.biz.gen.model.tymschemafield;

import com.ksptool.assembly.entity.exception.AuthException;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import com.ksptool.bio.biz.core.common.jpa.SnowflakeIdGenerated;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@Entity
@Table(name = "gen_tym_schema_field")
@EntityListeners(AuditingEntityListener.class)
public class TymSchemaFieldPo {

    @Id
    @SnowflakeIdGenerated
    @Column(name = "id", nullable = false, comment = "主键ID")
    private Long id;

    @Column(name = "type_schema_id", nullable = false, comment = "类型映射方案ID")
    private Long typeSchemaId;

    @Column(name = "source", nullable = false, length = 80, comment = "匹配源类型")
    private String source;

    @Column(name = "target", nullable = false, length = 80, comment = "匹配目标类型")
    private String target;

    @Column(name = "seq", nullable = false, comment = "排序")
    private Integer seq;

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


    @PrePersist
    private void onCreate() throws AuthException {


    }

    @PreUpdate
    private void onUpdate() throws AuthException {
    }
}
