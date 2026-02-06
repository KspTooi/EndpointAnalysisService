package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.noticercd.NoticeRcdPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRcdRepository extends JpaRepository<NoticeRcdPo, Long> {

    @Query("""
            SELECT u FROM NoticeRcdPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.noticeId} IS NULL OR u.noticeId  = :#{#po.noticeId} )
            AND (:#{#po.userId} IS NULL OR u.userId  = :#{#po.userId} )
            AND (:#{#po.readTime} IS NULL OR u.readTime  = :#{#po.readTime} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<NoticeRcdPo> getNoticeRcdList(@Param("po") NoticeRcdPo po, Pageable pageable);
}
