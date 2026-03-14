package com.ksptool.bio.biz.assembly.repository;

import com.ksptool.bio.biz.assembly.model.outmodelpoly.OutModelPolyPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

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
    List<OutModelPolyPo> getOutModelPolyList(@Param("po") OutModelPolyPo po);

    /**
     * 清空输出方案聚合模型
     *
     * @param outputSchemaId 输出方案ID
     * @return 删除条
     */
    @Query("""
            DELETE FROM OutModelPolyPo u WHERE u.outputSchemaId = :outputSchemaId
            """)
    @Modifying
    int clearByOutputSchemaId(@Param("outputSchemaId") Long outputSchemaId);

    /**
     * 查询输出方案绑定的全部聚合模型
     *
     * @param outputSchemaId 输出方案ID
     * @return 输出方案绑定的全部聚合模型
     */
    @Query("""
            SELECT u FROM OutModelPolyPo u WHERE u.outputSchemaId = :outputSchemaId ORDER BY u.seq ASC
            """)
    List<OutModelPolyPo> getByOutputSchemaId(@Param("outputSchemaId") Long outputSchemaId);
}
