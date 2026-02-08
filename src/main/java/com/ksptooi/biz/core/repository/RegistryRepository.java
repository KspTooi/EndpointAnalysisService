package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.registry.RegistryPo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegistryRepository extends JpaRepository<RegistryPo, Long> {

    @Query("""
            SELECT u FROM RegistryPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.parentId} IS NULL OR u.parentId  = :#{#po.parentId} )
            AND (:#{#po.keyPath} IS NULL OR u.keyPath  LIKE CONCAT('%', :#{#po.keyPath}, '%') )
            AND (:#{#po.nkey} IS NULL OR u.nkey  LIKE CONCAT('%', :#{#po.nkey}, '%') )
            AND (:#{#po.nvalueKind} IS NULL OR u.nvalueKind  = :#{#po.nvalueKind} )
            AND (:#{#po.nvalue} IS NULL OR u.nvalue  LIKE CONCAT('%', :#{#po.nvalue}, '%') )
            AND (:#{#po.label} IS NULL OR u.label  LIKE CONCAT('%', :#{#po.label}, '%') )
            AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.remark} IS NULL OR u.remark  LIKE CONCAT('%', :#{#po.remark}, '%') )
            AND (:#{#po.metadata} IS NULL OR u.metadata  LIKE CONCAT('%', :#{#po.metadata}, '%') )
            AND (:#{#po.isSystem} IS NULL OR u.isSystem  = :#{#po.isSystem} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<RegistryPo> getRegistryList(@Param("po") RegistryPo po, Pageable pageable);


    /**
     * 根据ID列表统计注册表数量
     * @param ids ID列表
     * @return 注册表数量
     */
    @Query("""
            SELECT COUNT(u) FROM RegistryPo u
            WHERE u.id IN :ids
            """)
    int countByIds(@Param("ids") List<Long> ids);


    /**
     * 根据父级ID统计注册表数量
     * @param parentId 父级ID
     * @return 注册表数量
     */
    @Query("""
            SELECT COUNT(u) FROM RegistryPo u
            WHERE u.parentId = :parentId
            """)
    int countByParentId(@Param("parentId") Long parentId);

    /**
     * 根据键统计注册表数量
     * @param nkey 键
     * @return 注册表数量
     */
    @Query("""
            SELECT COUNT(u) FROM RegistryPo u
            WHERE u.nkey = :nkey
            """)
    int countByNkey(@Param("nkey") String nkey);

    /**
     * 根据KEY的全路径统计注册表数量
     * @param keyPath KEY的全路径
     * @return 注册表数量
     */
    @Query("""
            SELECT COUNT(u) FROM RegistryPo u
            WHERE u.keyPath = :keyPath
            """)
    int countByKeyPath(@Param("keyPath") String keyPath);

}
