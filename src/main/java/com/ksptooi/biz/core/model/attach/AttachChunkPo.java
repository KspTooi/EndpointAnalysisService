package com.ksptooi.biz.core.model.attach;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.Comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "core_attach_chunk", indexes = {
        @Index(name = "uk_attach_chunk", columnList = "attach_id,chunk_id", unique = true)
})
@Comment("附件分块数据")
public class AttachChunkPo {

    @Column(name = "id")
    @Id
    @Comment("主键ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attach_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Comment("附件ID")
    private AttachPo attach;

    @Column(name = "chunk_id", nullable = false)
    @Comment("分块ID")
    private Long chunkId;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

}
