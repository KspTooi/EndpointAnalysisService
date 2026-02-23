package com.ksptool.bio.biz.rdbg.repository;

import com.ksptool.bio.biz.rdbg.model.collectionhistory.CollectionHistoryPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionHistoryRepository extends JpaRepository<CollectionHistoryPo, Long> {

    @Query("""
            SELECT u FROM CollectionHistoryPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.companyId} IS NULL OR u.companyId  = :#{#po.companyId} )
            AND (:#{#po.collectionId} IS NULL OR u.collectionId  = :#{#po.collectionId} )
            AND (:#{#po.reqUrl} IS NULL OR u.reqUrl  = :#{#po.reqUrl} )
            AND (:#{#po.reqUrlParamsJson} IS NULL OR u.reqUrlParamsJson  = :#{#po.reqUrlParamsJson} )
            AND (:#{#po.reqMethod} IS NULL OR u.reqMethod  = :#{#po.reqMethod} )
            AND (:#{#po.reqHeaderJson} IS NULL OR u.reqHeaderJson  = :#{#po.reqHeaderJson} )
            AND (:#{#po.reqBodyJson} IS NULL OR u.reqBodyJson  = :#{#po.reqBodyJson} )
            AND (:#{#po.retHeaderJson} IS NULL OR u.retHeaderJson  = :#{#po.retHeaderJson} )
            AND (:#{#po.retBodyText} IS NULL OR u.retBodyText  = :#{#po.retBodyText} )
            AND (:#{#po.retHttpStatus} IS NULL OR u.retHttpStatus  = :#{#po.retHttpStatus} )
            AND (:#{#po.bizStatus} IS NULL OR u.bizStatus  = :#{#po.bizStatus} )
            AND (:#{#po.reqTime} IS NULL OR u.reqTime  = :#{#po.reqTime} )
            AND (:#{#po.retTime} IS NULL OR u.retTime  = :#{#po.retTime} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updaterId} IS NULL OR u.updaterId  = :#{#po.updaterId} )
            AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<CollectionHistoryPo> getCollectionHistoryList(@Param("po") CollectionHistoryPo po, Pageable pageable);
}
