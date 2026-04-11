package com.ksptool.bio.biz.assembly.repository;

import com.ksptool.bio.biz.assembly.model.opschema.OpSchemaPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OpSchemaRepository extends JpaRepository<OpSchemaPo, Long> {

    @Query("""
            SELECT u FROM OpSchemaPo u
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
    Page<OpSchemaPo> getOpSchemaList(@Param("po") OpSchemaPo po, Pageable pageable);

    /**
     * 根据映射方案ID统计输出方案数量
     *
     * @param tymSchemaId 映射方案ID
     * @return 输出方案数量
     */
    @Query("""
            SELECT COUNT(u) FROM OpSchemaPo u WHERE u.typeSchemaId = :typeSchemaId
            """)
    int countByTymSchemaId(@Param("typeSchemaId") Long typeSchemaId);
}
