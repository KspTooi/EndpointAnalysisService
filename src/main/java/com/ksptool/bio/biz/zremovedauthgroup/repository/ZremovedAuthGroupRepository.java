package com.ksptool.bio.biz.zremovedauthgroup.repository;

import com.ksptool.bio.biz.zremovedauthgroup.model.ZremovedAuthGroupPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface ZremovedAuthGroupRepository extends JpaRepository<ZremovedAuthGroupPo, Long>{

    @Query("""
    SELECT u FROM ZremovedAuthGroupPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.code} IS NULL OR u.code  LIKE CONCAT('%', :#{#po.code}, '%') )
    AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
    AND (:#{#po.remark} IS NULL OR u.remark  LIKE CONCAT('%', :#{#po.remark}, '%') )
    AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
    AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
    AND (:#{#po.isSystem} IS NULL OR u.isSystem  = :#{#po.isSystem} )
    AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
    ORDER BY u.updateTime DESC
    """)
    Page<ZremovedAuthGroupPo> getZremovedAuthGroupList(@Param("po") ZremovedAuthGroupPo po, Pageable pageable);
}
