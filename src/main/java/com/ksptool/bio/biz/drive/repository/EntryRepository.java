package com.ksptool.bio.biz.drive.repository;

import com.ksptool.bio.biz.drive.model.driveentry.EntryPo;
import com.ksptool.bio.biz.drive.model.driveentry.vo.GetDriveInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface EntryRepository extends JpaRepository<EntryPo, Long>, JpaSpecificationExecutor<EntryPo> {

    /**
     * 获取云盘信息
     *
     * @return 云盘信息
     */
    @Query("""
            SELECT new com.ksptool.bio.biz.drive.model.driveentry.vo.GetDriveInfo(
                CAST(0 AS long),
                CAST(COALESCE(SUM(CASE WHEN u.kind = 0 THEN u.attachSize ELSE CAST(0 AS long) END), CAST(0 AS long)) AS long),
                COUNT(u)
            )
            FROM EntryPo u
            """)
    GetDriveInfo getDriveInfo();


    @Query("""
            SELECT u FROM EntryPo u
            WHERE
            (:#{#keyword} IS NOT NULL OR ((:parentId IS NULL AND u.parent IS NULL) OR u.parent.id = :parentId)) AND
            (:#{#keyword} IS NULL OR u.name LIKE CONCAT('%', :keyword, '%'))
            ORDER BY u.kind DESC,u.name ASC
            """)
    Page<EntryPo> getEntryList(@Param("parentId") Long parentId, @Param("keyword") String keyword, Pageable pageable);

    @Query("""
            SELECT COUNT(t) FROM EntryPo t
            WHERE
            ((:parentId IS NULL AND t.parent IS NULL) OR t.parent.id = :parentId) AND
            t.name = :name
            """)
    long countByName(@Param("parentId") Long parentId, @Param("name") String name);


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
    int updateEntryAttachStatusByEntryIds(@Param("entryIds") List<Long> entryIds, @Param("status") Integer status);

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

    /**
     * 根据ID和公司ID查询条目
     *
     * @param ids 条目IDS
     * @return 条目
     */
    @Query("""
            SELECT t FROM EntryPo t
            WHERE t.id IN :ids
            """)
    List<EntryPo> getEntryByIds(@Param("ids") List<Long> ids);

    /**
     * 根据ID和公司ID查询条目
     *
     * @param id 条目ID
     * @return 条目
     */
    @Query("""
            SELECT t FROM EntryPo t WHERE t.id = :id
            """)
    EntryPo getEntryById(@Param("id") Long id);

    /**
     * 根据名称和父级目录ID和公司ID统计条目数量
     *
     * @param parentId 父级目录ID
     * @param name     名称
     * @return 条目数量
     */
    @Query("""
            SELECT COUNT(t) FROM EntryPo t
            WHERE ((:parentId IS NULL AND t.parent IS NULL) OR t.parent.id = :parentId) AND
            t.name = :name
            """)
    Long countByNameParentId(@Param("parentId") Long parentId, @Param("name") String name);


    /**
     * 根据给定的名称查找父级ID下是否存在同名条目
     *
     * @param names    名称列表
     * @param parentId 父级目录ID
     * @return 匹配的同名条目名称列表
     */
    @Query("""
            SELECT DISTINCT t.name FROM EntryPo t
            WHERE ((:parentId IS NULL AND t.parent IS NULL) OR t.parent.id = :parentId) AND
            t.name IN :names
            ORDER BY t.name DESC
            """)
    Set<String> matchNamesByParentId(@Param("names") Set<String> names, @Param("parentId") Long parentId);


    /**
     * 根据名称和父级目录ID和公司ID逻辑删除条目
     *
     * @param names    名称列表
     * @param parentId 父级目录ID
     * @return 更新条数
     */
    @Modifying
    @Query("""
            UPDATE EntryPo t SET t.deleteTime = now() WHERE ((:parentId IS NULL AND t.parent IS NULL) OR t.parent.id = :parentId) AND t.name IN :names AND t.deleteTime IS NULL
            """)
    int removeByNameAndParentId(@Param("names") Set<String> names, @Param("parentId") Long parentId);

    /**
     * 根据ID列表获取名称列表
     *
     * @param ids ID列表
     * @return 名称列表
     */
    @Query("""
            SELECT DISTINCT t.name FROM EntryPo t WHERE t.id IN :ids
            """)
    Set<String> getNamesByIds(@Param("ids") List<Long> ids);

}
