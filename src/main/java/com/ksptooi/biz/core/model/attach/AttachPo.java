package com.ksptooi.biz.core.model.attach;

import com.ksptooi.commons.utils.IdWorker;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Comment;
import com.ksptooi.biz.core.service.AuthService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name = "core_attach")
public class AttachPo {

    @Column(name = "id")
    @Id
    private Long id;

    @Column(name = "name", length = 128, nullable = false)
    @Comment("文件名")
    private String name;

    @Column(name = "kind", length = 64, nullable = false)
    @Comment("文件类型")
    private String kind;

    @Column(name = "suffix", length = 128)
    @Comment("文件后缀")
    private String suffix;

    @Column(name = "path", length = 256, nullable = false)
    @Comment("文件路径")
    private String path;

    @Column(name = "sha256", length = 320, nullable = false)
    @Comment("文件SHA256")
    private String sha256;

    @Column(name = "total_size", nullable = false)
    @Comment("文件总大小")
    private Long totalSize;

    @Column(name = "receive_size", nullable = false)
    @Comment("已接收大小")
    private Long receiveSize;

    @Column(name = "status", length = 1, nullable = false)
    @Comment("状态 0:预检文件 1:区块不完整 2:校验中 3:有效")
    private Integer status;

    @Column(name = "verify_time")
    @Comment("校验时间")
    private LocalDateTime verifyTime;

    @Column(name = "create_time", nullable = false)
    @Comment("创建时间")
    private LocalDateTime createTime;

    @Column(name = "creator_id", nullable = false)
    @Comment("创建人ID")
    private Long creatorId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "attach", cascade = CascadeType.ALL, orphanRemoval = true)
    @Comment("分块数据")
    @OrderBy("chunkId ASC")
    @BatchSize(size = 100)
    private List<AttachChunkPo> chunks;

    public void addChunk(long chunkId){

        if(chunks == null){
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
    public void prePersist() {
        createTime = LocalDateTime.now();
        if (this.creatorId == null) {
            this.creatorId = AuthService.getCurrentUserId();
        }
    }
    

}
