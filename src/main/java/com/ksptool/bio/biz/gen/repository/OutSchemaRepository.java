package com.ksptool.bio.biz.gen.repository;

import com.ksptool.bio.biz.gen.model.outschema.OutSchemaPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface OutSchemaRepository extends JpaRepository<OutSchemaPo, Long> {

    @Query("""
            SELECT u FROM OutSchemaPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.dataSourceId} IS NULL OR u.dataSourceId  = :#{#po.dataSourceId} )
            AND (:#{#po.typeSchemaId} IS NULL OR u.typeSchemaId  = :#{#po.typeSchemaId} )
            AND (:#{#po.inputScmId} IS NULL OR u.inputScmId  = :#{#po.inputScmId} )
            AND (:#{#po.outputScmId} IS NULL OR u.outputScmId  = :#{#po.outputScmId} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.modelName} IS NULL OR u.modelName  LIKE CONCAT('%', :#{#po.modelName}, '%') )
            AND (:#{#po.tableName} IS NULL OR u.tableName  LIKE CONCAT('%', :#{#po.tableName}, '%') )
            AND (:#{#po.removeTablePrefix} IS NULL OR u.removeTablePrefix  LIKE CONCAT('%', :#{#po.removeTablePrefix}, '%') )
            AND (:#{#po.permCodePrefix} IS NULL OR u.permCodePrefix  LIKE CONCAT('%', :#{#po.permCodePrefix}, '%') )
            AND (:#{#po.policyOverride} IS NULL OR u.policyOverride  = :#{#po.policyOverride} )
            AND (:#{#po.baseInput} IS NULL OR u.baseInput  LIKE CONCAT('%', :#{#po.baseInput}, '%') )
            AND (:#{#po.baseOutput} IS NULL OR u.baseOutput  LIKE CONCAT('%', :#{#po.baseOutput}, '%') )
            AND (:#{#po.remark} IS NULL OR u.remark  LIKE CONCAT('%', :#{#po.remark}, '%') )
            AND (:#{#po.fieldCountOrigin} IS NULL OR u.fieldCountOrigin  = :#{#po.fieldCountOrigin} )
            AND (:#{#po.fieldCountPoly} IS NULL OR u.fieldCountPoly  = :#{#po.fieldCountPoly} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            ORDER BY u.updateTime DESC
            """)
    Page<OutSchemaPo> getOutSchemaList(@Param("po") OutSchemaPo po, Pageable pageable);
}
