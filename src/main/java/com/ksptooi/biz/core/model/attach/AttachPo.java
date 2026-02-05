package com.ksptooi.biz.core.model.attach;

import com.ksptooi.commons.utils.IdWorker;
import com.ksptool.assembly.entity.exception.AuthException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ksptooi.biz.core.service.SessionService.session;


@Getter
@Setter
@Entity
@Table(name = "core_attach")
public class AttachPo {

    @Column(name = "id", comment = "主键ID")
    @Id
    private Long id;

    @Column(name = "name", length = 128, nullable = false, comment = "文件名")
    private String name;

    @Column(name = "kind", length = 64, nullable = false, comment = "文件类型")
    private String kind;

    @Column(name = "suffix", length = 128, comment = "文件后缀")
    private String suffix;

    @Column(name = "path", length = 256, nullable = false, comment = "文件路径")
    private String path;

    @Column(name = "sha256", length = 320, nullable = false, comment = "文件SHA256")
    private String sha256;

    @Column(name = "total_size", nullable = false, comment = "文件总大小")
    private Long totalSize;

    @Column(name = "receive_size", nullable = false, comment = "已接收大小")
    private Long receiveSize;

    @Column(name = "status", length = 1, nullable = false, comment = "状态 0:预检文件 1:区块不完整 2:校验中 3:有效")
    private Integer status;

    @Column(name = "verify_time", comment = "校验时间")
    private LocalDateTime verifyTime;

    @Column(name = "create_time", nullable = false, comment = "创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false, comment = "创建人ID")
    private Long creatorId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attach", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("chunkId ASC")
    @BatchSize(size = 100)
    private List<AttachChunkPo> chunks;

    public void addChunk(long chunkId) {

        if (chunks == null) {
            chunks = new ArrayList<>();
        }

        var chunk = new AttachChunkPo();
        chunk.setId(IdWorker.nextId());
        chunk.setAttach(this);
        chunk.setChunkId(chunkId);
        chunk.setCreateTime(LocalDateTime.now());
        chunks.add(chunk);
    }

    @PrePersist
    public void prePersist() throws AuthException {

        if (this.id == null) {
            this.id = IdWorker.nextId();
        }

        createTime = LocalDateTime.now();
        if (this.creatorId == null) {
            this.creatorId = session().getUserId();
        }
    }


}
