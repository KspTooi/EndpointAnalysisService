package com.ksptooi.biz.drive.repository;

import com.ksptooi.biz.drive.model.po.EntryPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface EntryRepository extends JpaRepository<EntryPo, Long>{

    @Query("""
    SELECT u FROM EntryPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.companyId} IS NULL OR u.companyId  = :#{#po.companyId} )
    AND (:#{#po.parentId} IS NULL OR u.parentId  = :#{#po.parentId} )
    AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
    AND (:#{#po.kind} IS NULL OR u.kind  = :#{#po.kind} )
    AND (:#{#po.attachId} IS NULL OR u.attachId  = :#{#po.attachId} )
    AND (:#{#po.attachSize} IS NULL OR u.attachSize  = :#{#po.attachSize} )
    AND (:#{#po.attachSuffix} IS NULL OR u.attachSuffix  = :#{#po.attachSuffix} )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
    ORDER BY u.updateTime DESC
    """)
    Page<EntryPo> getEntryList(@Param("po") EntryPo po, Pageable pageable);
}
