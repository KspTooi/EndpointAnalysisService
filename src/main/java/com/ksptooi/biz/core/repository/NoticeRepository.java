package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.notice.NoticePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NoticeRepository extends JpaRepository<NoticePo, Long> {

    @Query("""
            SELECT u FROM NoticePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.title} IS NULL OR u.title  LIKE CONCAT('%', :#{#po.title}, '%') )
            AND (:#{#po.kind} IS NULL OR u.kind  = :#{#po.kind} )
            AND (:#{#po.content} IS NULL OR u.content  LIKE CONCAT('%', :#{#po.content}, '%') )
            AND (:#{#po.priority} IS NULL OR u.priority  = :#{#po.priority} )
            AND (:#{#po.category} IS NULL OR u.category  LIKE CONCAT('%', :#{#po.category}, '%') )
            AND (:#{#po.senderId} IS NULL OR u.senderId  = :#{#po.senderId} )
            AND (:#{#po.senderName} IS NULL OR u.senderName  LIKE CONCAT('%', :#{#po.senderName}, '%') )
            AND (:#{#po.forward} IS NULL OR u.forward  LIKE CONCAT('%', :#{#po.forward}, '%') )
            AND (:#{#po.params} IS NULL OR u.params  LIKE CONCAT('%', :#{#po.params}, '%') )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            ORDER BY u.createTime DESC
            """)
    Page<NoticePo> getNoticeList(@Param("po") NoticePo po, Pageable pageable);
}
