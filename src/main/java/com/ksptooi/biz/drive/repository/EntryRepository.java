package com.ksptooi.biz.drive.repository;

import com.ksptooi.biz.drive.model.EntryPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntryRepository extends JpaRepository<EntryPo, Long> {


    @Query("""
            SELECT u FROM EntryPo u
            WHERE
            ((:parentId IS NULL AND u.parent IS NULL) OR u.parent.id = :parentId) AND
            u.companyId = :companyId AND
            (:#{#keyword} IS NULL OR u.name LIKE CONCAT('%', :keyword, '%'))
            ORDER BY u.updateTime DESC
            """)
    Page<EntryPo> getEntryList(@Param("parentId") Long parentId, @Param("keyword") String keyword, @Param("companyId") Long companyId, Pageable pageable);

    @Query("""
            SELECT COUNT(u) FROM EntryPo u
            WHERE
            u.parent.id = :parentId AND
            u.companyId = :companyId AND
            u.name = :name
            """)
    long countByName(@Param("companyId") Long companyId, @Param("parentId") Long parentId, @Param("name") String name);

    @Query("""
            SELECT COUNT(u) FROM EntryPo u
            WHERE
            u.parent.id = :parentId AND
            u.companyId = :companyId AND
            u.name = :name AND
            u.id != :id
            """)
    long countByNameIgnoreId(@Param("companyId") Long companyId, @Param("parentId") Long parentId, @Param("name") String name, @Param("id") Long id);


    /**
     * 根据条目IDS更新文件附件状态
     *
     * @param entryIds 条目IDS
     * @param status   文件附件状态
     * @return 更新条数
     */
    @Modifying
    @Query("""
            UPDATE EntryPo u SET u.attachStatus = :status WHERE u.id IN :entryIds
            """)
    long updateEntryAttachStatusByEntryIds(@Param("entryIds") List<Long> entryIds, @Param("status") Integer status);

    /**
     * 根据条目IDS删除条目
     *
     * @param entryIds 条目IDS
     * @return 删除条数
     */
    @Modifying
    @Query("""
            UPDATE EntryPo u SET u.deleteTime = now() WHERE u.id IN :entryIds AND u.deleteTime IS NULL
            """)
    int removeEntryByEntryIds(@Param("entryIds") List<Long> entryIds);

    /**
     * 查询需要同步的云盘条目
     *
     * @param limit 查询条数
     * @return 云盘条目列表
     */
    @Query("""
            SELECT t FROM EntryPo t
            WHERE
            t.kind = 0 AND
            t.attachStatus != 3 AND
            t.deleteTime IS NULL
            ORDER BY t.createTime DESC
            LIMIT :limit
            """)
    List<EntryPo> getNeedSyncEntryList(@Param("limit") int limit);

}
