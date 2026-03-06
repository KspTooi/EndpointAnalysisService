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
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.typeSchemaId} IS NULL OR u.typeSchemaId  = :#{#po.typeSchemaId} )
            AND (:#{#po.source} IS NULL OR u.source  LIKE CONCAT('%', :#{#po.source}, '%') )
            AND (:#{#po.target} IS NULL OR u.target  LIKE CONCAT('%', :#{#po.target}, '%') )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            ORDER BY u.updateTime DESC
            """)
    Page<TymSchemaFieldPo> getTymSchemaFieldList(@Param("po") TymSchemaFieldPo po, Pageable pageable);
}
