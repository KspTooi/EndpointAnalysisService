package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.routerule.po.RouteRulePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteRuleRepository extends JpaRepository<RouteRulePo, Long> {

    @Query("""
            SELECT u FROM RouteRulePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
            AND (:#{#po.matchType} IS NULL OR u.matchType  = :#{#po.matchType} )
            AND (:#{#po.matchKey} IS NULL OR u.matchKey  = :#{#po.matchKey} )
            AND (:#{#po.matchOperator} IS NULL OR u.matchOperator  = :#{#po.matchOperator} )
            AND (:#{#po.matchValue} IS NULL OR u.matchValue  = :#{#po.matchValue} )
            AND (:#{#po.routeServerId} IS NULL OR u.routeServerId  = :#{#po.routeServerId} )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.remark} IS NULL OR u.remark  = :#{#po.remark} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<RouteRulePo> getRouteRuleList(@Param("po") RouteRulePo po, Pageable pageable);
}
