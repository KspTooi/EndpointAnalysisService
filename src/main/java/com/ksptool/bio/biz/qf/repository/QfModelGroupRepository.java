package com.ksptool.bio.biz.qf.repository;

import com.ksptool.bio.biz.qf.model.qfmodelgroup.QfModelGroupPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QfModelGroupRepository extends JpaRepository<QfModelGroupPo, Long> {

    @Query("""
            SELECT u FROM QfModelGroupPo u
            WHERE
            (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%'))
            AND (:#{#po.code} IS NULL OR u.code LIKE CONCAT('%', :#{#po.code}, '%'))
            ORDER BY u.createTime DESC
            """ )
    Page<QfModelGroupPo> getQfModelGroupList(@Param("po") QfModelGroupPo po, Pageable pageable);
}
