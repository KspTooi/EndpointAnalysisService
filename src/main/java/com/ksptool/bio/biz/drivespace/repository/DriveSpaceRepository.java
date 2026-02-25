package com.ksptool.bio.biz.drivespace.repository;

import com.ksptool.bio.biz.drivespace.model.DriveSpacePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DriveSpaceRepository extends JpaRepository<DriveSpacePo, Long>{

    @Query("""
    SELECT u FROM DriveSpacePo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.rootId} IS NULL OR u.rootId  = :#{#po.rootId} )
    AND (:#{#po.deptId} IS NULL OR u.deptId  = :#{#po.deptId} )
    AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
    AND (:#{#po.remark} IS NULL OR u.remark  LIKE CONCAT('%', :#{#po.remark}, '%') )
    AND (:#{#po.quotaLimit} IS NULL OR u.quotaLimit  = :#{#po.quotaLimit} )
    AND (:#{#po.quotaUsed} IS NULL OR u.quotaUsed  = :#{#po.quotaUsed} )
    AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
    AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
    ORDER BY u.updateTime DESC
    """)
    Page<DriveSpacePo> getDriveSpaceList(@Param("po") DriveSpacePo po, Pageable pageable);
}
