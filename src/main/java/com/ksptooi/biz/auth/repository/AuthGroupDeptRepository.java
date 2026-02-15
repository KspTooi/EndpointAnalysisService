package com.ksptooi.biz.auth.repository;

import com.ksptooi.biz.auth.model.AuthGroupDeptPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthGroupDeptRepository extends JpaRepository<AuthGroupDeptPo, AuthGroupDeptPo.Pk> {

    @Query("""
            SELECT u FROM AuthGroupDeptPo u
            WHERE
            (:#{#po.groupId} IS NULL OR u.groupId = :#{#po.groupId})
            AND (:#{#po.deptId} IS NULL OR u.deptId = :#{#po.deptId})
            AND (:#{#po.createTime} IS NULL OR u.createTime = :#{#po.createTime})
            ORDER BY u.createTime DESC
            """)
    Page<AuthGroupDeptPo> getAuthGroupDeptList(@Param("po") AuthGroupDeptPo po, Pageable pageable);
}
