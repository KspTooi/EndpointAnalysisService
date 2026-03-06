package com.ksptool.bio.biz.gentymschemafield.repository;

import com.ksptool.bio.biz.gentymschemafield.model.GenTymSchemaFieldPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface GenTymSchemaFieldRepository extends JpaRepository<GenTymSchemaFieldPo, Long>{

    @Query("""
    SELECT u FROM GenTymSchemaFieldPo u
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
    Page<GenTymSchemaFieldPo> getGenTymSchemaFieldList(@Param("po") GenTymSchemaFieldPo po, Pageable pageable);
}
