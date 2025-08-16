package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.filter.SimpleFilterOperationPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SimpleFilterOperationRepository extends JpaRepository<SimpleFilterOperationPo, Long> {

    @Query("""
            SELECT u FROM SimpleFilterOperationPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.f} IS NULL OR u.f  = :#{#po.f} )
            AND (:#{#po.kind} IS NULL OR u.kind  = :#{#po.kind} )
            AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.t} IS NULL OR u.t  = :#{#po.t} )
            AND (:#{#po.target} IS NULL OR u.target  = :#{#po.target} )
            ORDER BY u.updateTime DESC
            """)
    Page<SimpleFilterOperationPo> getSimpleFilterOperationList(@Param("po") SimpleFilterOperationPo po, Pageable pageable);
}
