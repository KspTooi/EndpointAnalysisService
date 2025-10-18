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
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.matchValue} IS NULL OR u.matchValue  = :#{#po.matchValue} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<RouteRulePo> getRouteRuleList(@Param("po") RouteRulePo po, Pageable pageable);
}
