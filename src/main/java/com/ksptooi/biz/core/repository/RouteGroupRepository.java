package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.routegroup.po.RouteGroupPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteGroupRepository extends JpaRepository<RouteGroupPo, Long> {

    @Query("""
            SELECT u FROM RouteGroupPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.autoDegradation} IS NULL OR u.autoDegradation  = :#{#po.autoDegradation} )
            AND (:#{#po.loadBalance} IS NULL OR u.loadBalance  = :#{#po.loadBalance} )
            AND (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.remark} IS NULL OR u.remark LIKE CONCAT('%', :#{#po.remark}, '%') )
            ORDER BY u.updateTime DESC
            """)
    Page<RouteGroupPo> getRouteGroupList(@Param("po") RouteGroupPo po, Pageable pageable);
}
