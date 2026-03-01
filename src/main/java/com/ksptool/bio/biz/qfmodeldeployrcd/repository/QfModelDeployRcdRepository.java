package com.ksptool.bio.biz.qfmodeldeployrcd.repository;

import com.ksptool.bio.biz.qfmodeldeployrcd.model.QfModelDeployRcdPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface QfModelDeployRcdRepository extends JpaRepository<QfModelDeployRcdPo, Long>{

    @Query("""
    SELECT u FROM QfModelDeployRcdPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.rootId} IS NULL OR u.rootId  = :#{#po.rootId} )
    AND (:#{#po.deptId} IS NULL OR u.deptId  = :#{#po.deptId} )
    AND (:#{#po.modelId} IS NULL OR u.modelId  = :#{#po.modelId} )
    AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
    AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
    AND (:#{#po.bpmnXml} IS NULL OR u.bpmnXml  LIKE CONCAT('%', :#{#po.bpmnXml}, '%') )
    AND (:#{#po.dataVersion} IS NULL OR u.dataVersion  = :#{#po.dataVersion} )
    AND (:#{#po.engDeploymentId} IS NULL OR u.engDeploymentId  LIKE CONCAT('%', :#{#po.engDeploymentId}, '%') )
    AND (:#{#po.engProcessDefId} IS NULL OR u.engProcessDefId  LIKE CONCAT('%', :#{#po.engProcessDefId}, '%') )
    AND (:#{#po.engDeployResult} IS NULL OR u.engDeployResult  LIKE CONCAT('%', :#{#po.engDeployResult}, '%') )
    AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
    AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
    ORDER BY u.updateTime DESC
    """)
    Page<QfModelDeployRcdPo> getQfModelDeployRcdList(@Param("po") QfModelDeployRcdPo po, Pageable pageable);
}
