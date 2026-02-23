package com.ksptool.bio.biz.relay.repository;

import com.ksptool.bio.biz.relay.model.relayserverroute.po.RelayServerRoutePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RelayServerRouteRepository extends JpaRepository<RelayServerRoutePo, Long> {

    @Query("""
            SELECT u FROM RelayServerRoutePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<RelayServerRoutePo> getRelayServerRouteList(@Param("po") RelayServerRoutePo po, Pageable pageable);
}
