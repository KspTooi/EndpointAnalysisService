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


    /**
     * 查询输出方案原始字段列表
     * @param po 查询参数
     * @return 输出方案原始字段列表
     */
    @Query("""
            SELECT u FROM OutModelOriginPo u
            WHERE
            (:#{#po.outputSchemaId} IS NULL OR u.outputSchemaId  = :#{#po.outputSchemaId} )
            ORDER BY u.seq ASC
            """)
    Page<OutModelOriginPo> getOutModelOriginList(@Param("po") OutModelOriginPo po);
}
