package com.ksptool.bio.biz.gen.repository;

import com.ksptool.bio.biz.gen.model.outmodelorigin.OutModelOriginPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface OutModelOriginRepository extends JpaRepository<OutModelOriginPo, Long> {

    /**
     * 查询输出方案原始字段
     * @param outputSchemaId 输出方案ID
     * @return 输出方案原始字段
     */
    @Query("""
            SELECT u FROM OutModelOriginPo u
            WHERE u.outputSchemaId = :outputSchemaId
            ORDER BY u.seq ASC
            """)
    List<OutModelOriginPo> getOmoByOutputSchemaId(Long outputSchemaId);



    @Query("""
            SELECT u FROM OutModelOriginPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.outputSchemaId} IS NULL OR u.outputSchemaId  = :#{#po.outputSchemaId} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.kind} IS NULL OR u.kind  LIKE CONCAT('%', :#{#po.kind}, '%') )
            AND (:#{#po.length} IS NULL OR u.length  LIKE CONCAT('%', :#{#po.length}, '%') )
            AND (:#{#po.require} IS NULL OR u.require  = :#{#po.require} )
            AND (:#{#po.remark} IS NULL OR u.remark  LIKE CONCAT('%', :#{#po.remark}, '%') )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            ORDER BY u.updateTime DESC
            """)
    Page<OutModelOriginPo> getOutModelOriginList(@Param("po") OutModelOriginPo po, Pageable pageable);
}
