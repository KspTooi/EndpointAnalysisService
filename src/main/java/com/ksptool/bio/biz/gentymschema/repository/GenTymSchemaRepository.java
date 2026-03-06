package com.ksptool.bio.biz.gentymschema.repository;

import com.ksptool.bio.biz.gentymschema.model.GenTymSchemaPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface GenTymSchemaRepository extends JpaRepository<GenTymSchemaPo, Long>{

    @Query("""
    SELECT u FROM GenTymSchemaPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
    AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
    AND (:#{#po.mapSource} IS NULL OR u.mapSource  LIKE CONCAT('%', :#{#po.mapSource}, '%') )
    AND (:#{#po.mapTarget} IS NULL OR u.mapTarget  LIKE CONCAT('%', :#{#po.mapTarget}, '%') )
    AND (:#{#po.typeCount} IS NULL OR u.typeCount  = :#{#po.typeCount} )
    AND (:#{#po.defaultType} IS NULL OR u.defaultType LIKE CONCAT('%', :#{#po.defaultType}, '%') )
    AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
    AND (:#{#po.remark} IS NULL OR u.remark  LIKE CONCAT('%', :#{#po.remark}, '%') )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
    ORDER BY u.updateTime DESC
    """)
    Page<GenTymSchemaPo> getGenTymSchemaList(@Param("po") GenTymSchemaPo po, Pageable pageable);
}
