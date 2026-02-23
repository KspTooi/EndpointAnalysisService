package com.ksptool.bio.biz.relay.repository;

import com.ksptool.bio.biz.relay.model.routeserver.po.RouteServerPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteServerRepository extends JpaRepository<RouteServerPo, Long> {

    @Query("""
            SELECT u FROM RouteServerPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.host} IS NULL OR u.host LIKE CONCAT('%', :#{#po.host}, '%') )
            AND (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.port} IS NULL OR u.port  = :#{#po.port} )
            AND (:#{#po.remark} IS NULL OR u.remark LIKE CONCAT('%', :#{#po.remark}, '%') )
            AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<RouteServerPo> getRouteServerList(@Param("po") RouteServerPo po, Pageable pageable);
}
