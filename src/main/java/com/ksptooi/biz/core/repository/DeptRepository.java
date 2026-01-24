package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.dept.DeptPo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeptRepository extends JpaRepository<DeptPo, Long> {



    @Query("""
            SELECT u FROM DeptPo u
            ORDER BY u.seq ASC
            """)
    List<DeptPo> getDeptListOrderBySeq();



    @Query("""
            SELECT u FROM DeptPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.parentId} IS NULL OR u.parentId  = :#{#po.parentId} )
            AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
            AND (:#{#po.principalId} IS NULL OR u.principalId  = :#{#po.principalId} )
            AND (:#{#po.principalName} IS NULL OR u.principalName  = :#{#po.principalName} )
            AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            ORDER BY u.updateTime DESC
            """)
    Page<DeptPo> getDeptList(@Param("po") DeptPo po, Pageable pageable);
}
