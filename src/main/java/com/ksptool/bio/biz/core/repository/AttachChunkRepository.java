package com.ksptool.bio.biz.core.repository;

import com.ksptool.bio.biz.core.model.attach.AttachChunkPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AttachChunkRepository extends JpaRepository<AttachChunkPo, Long> {

    /**
     * 查询指定附件和分块ID的重复应用次数
     *
     * @param attachId 附件ID
     * @param chunkId  分块ID
     * @return 记录数
     */
    @Query("""
            SELECT COUNT(t) FROM AttachChunkPo t
            WHERE
            t.attach.id = :attachId AND
            t.chunkId = :chunkId
            """)
    Long countByAttachIdAndChunkId(@Param("attachId") Long attachId, @Param("chunkId") Long chunkId);
}
