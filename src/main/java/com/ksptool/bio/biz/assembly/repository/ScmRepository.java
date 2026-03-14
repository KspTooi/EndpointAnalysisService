package com.ksptool.bio.biz.assembly.repository;

import com.ksptool.bio.biz.assembly.model.scm.ScmPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ScmRepository extends JpaRepository<ScmPo, Long> {

    @Query("""
            SELECT u FROM ScmPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.projectName} IS NULL OR u.projectName  LIKE CONCAT('%', :#{#po.projectName}, '%') )
            AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
            ORDER BY u.updateTime DESC
            """)
    Page<ScmPo> getScmList(@Param("po") ScmPo po, Pageable pageable);
}
