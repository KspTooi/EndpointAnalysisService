package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.epdoc.EndpointDocPo;
import com.ksptooi.biz.core.model.epdocsynclog.EndpointDocSyncLogPo;
import com.ksptooi.biz.core.model.epdocsynclog.GetEpDocSyncLogListDto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EndpointDocSyncLogRepository extends CrudRepository<EndpointDocSyncLogPo,Long> {

    @Query("""
            SELECT t FROM EndpointDocSyncLogPo t
            WHERE
            t.endpointDoc.id = :#{#dto.epDocId}
            ORDER BY t.createTime DESC
    """)
    Page<EndpointDocSyncLogPo> getEpSyncLogList(@Param("dto") GetEpDocSyncLogListDto dto, Pageable pageable);

}
