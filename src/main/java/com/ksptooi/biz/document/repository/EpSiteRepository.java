package com.ksptooi.biz.document.repository;

import com.ksptooi.biz.document.model.epsite.EpSitePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EpSiteRepository extends JpaRepository<EpSitePo, Long> {

    @Query("""
            SELECT u FROM EpSitePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.address} IS NULL OR u.address LIKE CONCAT('%', :#{#po.address}, '%') )
            AND (:#{#po.username} IS NULL OR u.username LIKE CONCAT('%', :#{#po.username}, '%') )
            AND (:#{#po.password} IS NULL OR u.password  = :#{#po.password} )
            AND (:#{#po.remark} IS NULL OR u.remark LIKE CONCAT('%', :#{#po.remark}, '%') )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            ORDER BY u.seq ASC
            """)
    Page<EpSitePo> getEpSiteList(@Param("po") EpSitePo po, Pageable pageable);

    @Query("""
            SELECT u FROM EpSitePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.address} IS NULL OR u.address LIKE CONCAT('%', :#{#po.address}, '%') )
            AND (:#{#po.username} IS NULL OR u.username LIKE CONCAT('%', :#{#po.username}, '%') )
            AND (:#{#po.password} IS NULL OR u.password  = :#{#po.password} )
            AND (:#{#po.remark} IS NULL OR u.remark LIKE CONCAT('%', :#{#po.remark}, '%') )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            ORDER BY u.seq ASC
            """)
    List<EpSitePo> getEpSiteListNotPage(@Param("po") EpSitePo po);

    /**
     * 根据站点名称查找站点
     * @param name 站点名称
     * @return 站点
     */
    @Query("""
            SELECT u FROM EpSitePo u
            WHERE u.name = :name
            """)
    EpSitePo getSiteByName(@Param("name") String name);

    /**
     * 根据站点名称查找站点，排除指定ID
     * @param name 站点名称
     * @param id 排除的ID
     * @return 站点
     */
    @Query("""
            SELECT u FROM EpSitePo u
            WHERE u.name = :name
              AND u.id != :id
            """)
    EpSitePo getSiteByNameExcludeId(@Param("name") String name, @Param("id") Long id);
}
