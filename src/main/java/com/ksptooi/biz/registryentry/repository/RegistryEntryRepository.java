package com.ksptooi.biz.registryentry.repository;

import com.ksptooi.biz.registryentry.model.RegistryEntryPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RegistryEntryRepository extends JpaRepository<RegistryEntryPo, Long>{

    @Query("""
    SELECT u FROM RegistryEntryPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.nodeId} IS NULL OR u.nodeId  = :#{#po.nodeId} )
    AND (:#{#po.nodeKeyPath} IS NULL OR u.nodeKeyPath  LIKE CONCAT('%', :#{#po.nodeKeyPath}, '%') )
    AND (:#{#po.ekey} IS NULL OR u.ekey  LIKE CONCAT('%', :#{#po.ekey}, '%') )
    AND (:#{#po.valueKeyPath} IS NULL OR u.valueKeyPath  LIKE CONCAT('%', :#{#po.valueKeyPath}, '%') )
    AND (:#{#po.valueKind} IS NULL OR u.valueKind  = :#{#po.valueKind} )
    AND (:#{#po.value} IS NULL OR u.value  LIKE CONCAT('%', :#{#po.value}, '%') )
    AND (:#{#po.label} IS NULL OR u.label  LIKE CONCAT('%', :#{#po.label}, '%') )
    AND (:#{#po.metadata} IS NULL OR u.metadata  LIKE CONCAT('%', :#{#po.metadata}, '%') )
    AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
    AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
    AND (:#{#po.isSystem} IS NULL OR u.isSystem  = :#{#po.isSystem} )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
    AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
    ORDER BY u.updateTime DESC
    """)
    Page<RegistryEntryPo> getRegistryEntryList(@Param("po") RegistryEntryPo po, Pageable pageable);
}
