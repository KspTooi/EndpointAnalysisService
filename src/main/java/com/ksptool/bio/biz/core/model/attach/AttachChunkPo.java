package com.ksptool.bio.biz.core.model.attach;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "core_attach_chunk", comment = "附件分块数据", indexes = {
        @Index(name = "uk_attach_chunk", columnList = "attach_id,chunk_id", unique = true)
})
public class AttachChunkPo {

    @Column(name = "id", comment = "主键ID")
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attach_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), comment = "附件ID")
    private AttachPo attach;

    @Column(name = "chunk_id", nullable = false, comment = "分块ID")
    private Long chunkId;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

}
