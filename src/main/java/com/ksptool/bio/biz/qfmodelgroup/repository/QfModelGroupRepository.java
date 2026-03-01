package com.ksptool.bio.biz.qfmodelgroup.repository;

import com.ksptool.bio.biz.qfmodelgroup.model.QfModelGroupPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface QfModelGroupRepository extends JpaRepository<QfModelGroupPo, Long>{

    @Query("""
    SELECT u FROM QfModelGroupPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.rootId} IS NULL OR u.rootId  = :#{#po.rootId} )
    AND (:#{#po.deptId} IS NULL OR u.deptId  = :#{#po.deptId} )
    AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
    AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
    AND (:#{#po.remark} IS NULL OR u.remark  LIKE CONCAT('%', :#{#po.remark}, '%') )
    AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
    AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
    ORDER BY u.updateTime DESC
    """)
    Page<QfModelGroupPo> getQfModelGroupList(@Param("po") QfModelGroupPo po, Pageable pageable);
}
