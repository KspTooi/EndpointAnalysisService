package com.ksptool.bio.biz.genoutblueprint.repository;

import com.ksptool.bio.biz.genoutblueprint.model.GenOutBlueprintPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface GenOutBlueprintRepository extends JpaRepository<GenOutBlueprintPo, Long>{

    @Query("""
    SELECT u FROM GenOutBlueprintPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
    AND (:#{#po.projectName} IS NULL OR u.projectName  LIKE CONCAT('%', :#{#po.projectName}, '%') )
    AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
    AND (:#{#po.scmUrl} IS NULL OR u.scmUrl  LIKE CONCAT('%', :#{#po.scmUrl}, '%') )
    AND (:#{#po.scmAuthKind} IS NULL OR u.scmAuthKind  = :#{#po.scmAuthKind} )
    AND (:#{#po.scmUsername} IS NULL OR u.scmUsername  LIKE CONCAT('%', :#{#po.scmUsername}, '%') )
    AND (:#{#po.scmPassword} IS NULL OR u.scmPassword  LIKE CONCAT('%', :#{#po.scmPassword}, '%') )
    AND (:#{#po.scmPk} IS NULL OR u.scmPk  LIKE CONCAT('%', :#{#po.scmPk}, '%') )
    AND (:#{#po.scmBranch} IS NULL OR u.scmBranch  LIKE CONCAT('%', :#{#po.scmBranch}, '%') )
    AND (:#{#po.scmBasePath} IS NULL OR u.scmBasePath  LIKE CONCAT('%', :#{#po.scmBasePath}, '%') )
    AND (:#{#po.remark} IS NULL OR u.remark  LIKE CONCAT('%', :#{#po.remark}, '%') )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
    ORDER BY u.updateTime DESC
    """)
    Page<GenOutBlueprintPo> getGenOutBlueprintList(@Param("po") GenOutBlueprintPo po, Pageable pageable);
}
