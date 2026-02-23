package com.ksptool.bio.biz.rdbg.repository;

import com.ksptool.bio.biz.rdbg.model.filter.SimpleFilterTriggerPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleFilterTriggerRepository extends JpaRepository<SimpleFilterTriggerPo, Long> {

    @Query("""
            SELECT u FROM SimpleFilterTriggerPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.kind} IS NULL OR u.kind  = :#{#po.kind} )
            AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.target} IS NULL OR u.target  = :#{#po.target} )
            AND (:#{#po.tk} IS NULL OR u.tk  = :#{#po.tk} )
            AND (:#{#po.tv} IS NULL OR u.tv  = :#{#po.tv} )
            ORDER BY u.updateTime DESC
            """)
    Page<SimpleFilterTriggerPo> getSimpleFilterTriggerList(@Param("po") SimpleFilterTriggerPo po, Pageable pageable);
}
