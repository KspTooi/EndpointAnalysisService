package com.ksptooi.biz.simplefilter.repository;

import com.ksptooi.biz.simplefilter.model.po.SimpleFilterPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface SimpleFilterRepository extends JpaRepository<SimpleFilterPo, Long>{

    @Query("""
    SELECT u FROM SimpleFilterPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
    AND (:#{#po.direction} IS NULL OR u.direction  = :#{#po.direction} )
    AND (:#{#po.triggerCondition} IS NULL OR u.triggerCondition  = :#{#po.triggerCondition} )
    AND (:#{#po.triggerKey} IS NULL OR u.triggerKey  = :#{#po.triggerKey} )
    AND (:#{#po.triggerValue} IS NULL OR u.triggerValue  = :#{#po.triggerValue} )
    AND (:#{#po.operation} IS NULL OR u.operation  = :#{#po.operation} )
    AND (:#{#po.getKey} IS NULL OR u.getKey  = :#{#po.getKey} )
    AND (:#{#po.getValue} IS NULL OR u.getValue  = :#{#po.getValue} )
    AND (:#{#po.injectType} IS NULL OR u.injectType  = :#{#po.injectType} )
    AND (:#{#po.injectKey} IS NULL OR u.injectKey  = :#{#po.injectKey} )
    AND (:#{#po.injectValue} IS NULL OR u.injectValue  = :#{#po.injectValue} )
    AND (:#{#po.injectStatusCode} IS NULL OR u.injectStatusCode  = :#{#po.injectStatusCode} )
    AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    ORDER BY u.updateTime DESC
    """)
    Page<SimpleFilterPo> getSimpleFilterList(@Param("po") SimpleFilterPo po, Pageable pageable);
}
