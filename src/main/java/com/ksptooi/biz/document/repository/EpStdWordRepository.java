package com.ksptooi.biz.document.repository;

import com.ksptooi.biz.document.model.epstdword.EpStdWordPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EpStdWordRepository extends JpaRepository<EpStdWordPo, Long> {

    @Query("""
            SELECT u FROM EpStdWordPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.sourceName} IS NULL OR u.sourceName  = :#{#po.sourceName} )
            AND (:#{#po.sourceNameFull} IS NULL OR u.sourceNameFull  = :#{#po.sourceNameFull} )
            AND (:#{#po.targetName} IS NULL OR u.targetName  = :#{#po.targetName} )
            AND (:#{#po.targetNameFull} IS NULL OR u.targetNameFull  = :#{#po.targetNameFull} )
            AND (:#{#po.remark} IS NULL OR u.remark  = :#{#po.remark} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            ORDER BY u.updateTime DESC
            """)
    Page<EpStdWordPo> getEpStdWordList(@Param("po") EpStdWordPo po, Pageable pageable);

    /**
     * 根据英文简称查找标准词
     * @param targetName 英文简称
     * @return 标准词
     */
    @Query("""
            SELECT u FROM EpStdWordPo u
            WHERE u.targetName = :targetName
            """)
    EpStdWordPo getStdWordByTargetName(@Param("targetName") String targetName);

    /**
     * 根据英文简称查找标准词，排除指定ID
     * @param targetName 英文简称
     * @param id 排除的ID
     * @return 标准词
     */
    @Query("""
            SELECT u FROM EpStdWordPo u
            WHERE u.targetName = :targetName
              AND u.id != :id
            """)
    EpStdWordPo getStdWordByTargetNameExcludeId(@Param("targetName") String targetName, @Param("id") Long id);
}
