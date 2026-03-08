package com.ksptool.bio.biz.gen.repository;

import com.ksptool.bio.biz.gen.model.outmodelpoly.OutModelPolyPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OutModelPolyRepository extends JpaRepository<OutModelPolyPo, Long> {

    @Query("""
            SELECT u FROM OutModelPolyPo u
            WHERE
            (:#{#po.outputSchemaId} IS NULL OR u.outputSchemaId  = :#{#po.outputSchemaId} )
            ORDER BY u.seq ASC
            """)
    Page<OutModelPolyPo> getOutModelPolyList(@Param("po") OutModelPolyPo po);
}
