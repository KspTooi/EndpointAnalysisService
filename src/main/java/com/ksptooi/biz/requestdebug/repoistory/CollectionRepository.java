package com.ksptooi.biz.requestdebug.repoistory;

import com.ksptooi.biz.requestdebug.model.collection.CollectionPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionRepository extends JpaRepository<CollectionPo, Long> {

    @Query("""
            SELECT u FROM CollectionPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.parentId} IS NULL OR u.parentId  = :#{#po.parentId} )
            AND (:#{#po.companyId} IS NULL OR u.companyId  = :#{#po.companyId} )
            AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
            AND (:#{#po.kind} IS NULL OR u.kind  = :#{#po.kind} )
            AND (:#{#po.reqUrl} IS NULL OR u.reqUrl  = :#{#po.reqUrl} )
            AND (:#{#po.reqUrlParamsJson} IS NULL OR u.reqUrlParamsJson  = :#{#po.reqUrlParamsJson} )
            AND (:#{#po.reqMethod} IS NULL OR u.reqMethod  = :#{#po.reqMethod} )
            AND (:#{#po.reqHeaderJson} IS NULL OR u.reqHeaderJson  = :#{#po.reqHeaderJson} )
            AND (:#{#po.reqBodyKind} IS NULL OR u.reqBodyKind  = :#{#po.reqBodyKind} )
            AND (:#{#po.reqBodyJson} IS NULL OR u.reqBodyJson  = :#{#po.reqBodyJson} )
            AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<CollectionPo> getCollectionList(@Param("po") CollectionPo po, Pageable pageable);
}
