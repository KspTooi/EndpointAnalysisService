package com.ksptool.bio.biz.gen.repository;

import com.ksptool.bio.biz.gen.model.outmodelpoly.OutModelPolyPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OutModelPolyRepository extends JpaRepository<OutModelPolyPo, Long> {

    @Query("""
            SELECT u FROM OutModelPolyPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.outputSchemaId} IS NULL OR u.outputSchemaId  = :#{#po.outputSchemaId} )
            AND (:#{#po.outputModelOriginId} IS NULL OR u.outputModelOriginId  = :#{#po.outputModelOriginId} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.kind} IS NULL OR u.kind  LIKE CONCAT('%', :#{#po.kind}, '%') )
            AND (:#{#po.length} IS NULL OR u.length  LIKE CONCAT('%', :#{#po.length}, '%') )
            AND (:#{#po.require} IS NULL OR u.require  = :#{#po.require} )
            AND (:#{#po.policyCrudJson} IS NULL OR u.policyCrudJson  LIKE CONCAT('%', :#{#po.policyCrudJson}, '%') )
            AND (:#{#po.policyQuery} IS NULL OR u.policyQuery  = :#{#po.policyQuery} )
            AND (:#{#po.policyView} IS NULL OR u.policyView  = :#{#po.policyView} )
            AND (:#{#po.placeholder} IS NULL OR u.placeholder  LIKE CONCAT('%', :#{#po.placeholder}, '%') )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            ORDER BY u.updateTime DESC
            """)
    Page<OutModelPolyPo> getOutModelPolyList(@Param("po") OutModelPolyPo po, Pageable pageable);
}
