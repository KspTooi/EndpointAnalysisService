package com.ksptooi.biz.authgroupdept.repository;

import com.ksptooi.biz.authgroupdept.model.AuthGroupDeptPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface AuthGroupDeptRepository extends JpaRepository<AuthGroupDeptPo, Long>{

    @Query("""
    SELECT u FROM AuthGroupDeptPo u
    WHERE
    (:#{#po.groupId} IS NULL OR u.groupId  = :#{#po.groupId} )
    AND (:#{#po.deptId} IS NULL OR u.deptId  = :#{#po.deptId} )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    ORDER BY u.updateTime DESC
    """)
    Page<AuthGroupDeptPo> getAuthGroupDeptList(@Param("po") AuthGroupDeptPo po, Pageable pageable);
}
