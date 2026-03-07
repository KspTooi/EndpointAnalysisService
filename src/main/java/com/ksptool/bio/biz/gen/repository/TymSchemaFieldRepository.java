package com.ksptool.bio.biz.gen.repository;

import com.ksptool.bio.biz.gen.model.tymschemafield.TymSchemaFieldPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TymSchemaFieldRepository extends JpaRepository<TymSchemaFieldPo, Long> {

    @Query("""
            SELECT u FROM TymSchemaFieldPo u
            WHERE
            (:#{#po.typeSchemaId} IS NULL OR u.typeSchemaId  = :#{#po.typeSchemaId} )
            ORDER BY u.seq,u.createTime DESC
            """)
    Page<TymSchemaFieldPo> getTymSchemaFieldList(@Param("po") TymSchemaFieldPo po, Pageable pageable);
}
