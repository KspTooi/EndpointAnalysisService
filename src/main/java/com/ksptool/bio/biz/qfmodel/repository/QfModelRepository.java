package com.ksptool.bio.biz.qfmodel.repository;

import com.ksptool.bio.biz.qfmodel.model.QfModelPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface QfModelRepository extends JpaRepository<QfModelPo, Long>{

    @Query("""
    SELECT u FROM QfModelPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.rootId} IS NULL OR u.rootId  = :#{#po.rootId} )
    AND (:#{#po.deptId} IS NULL OR u.deptId  = :#{#po.deptId} )
    AND (:#{#po.activeDeployId} IS NULL OR u.activeDeployId  = :#{#po.activeDeployId} )
    AND (:#{#po.groupId} IS NULL OR u.groupId  = :#{#po.groupId} )
    AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
    AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
    AND (:#{#po.bpmnXml} IS NULL OR u.bpmnXml  LIKE CONCAT('%', :#{#po.bpmnXml}, '%') )
    AND (:#{#po.dataVersion} IS NULL OR u.dataVersion  = :#{#po.dataVersion} )
    AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
    AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
    AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
    ORDER BY u.updateTime DESC
    """)
    Page<QfModelPo> getQfModelList(@Param("po") QfModelPo po, Pageable pageable);
}
