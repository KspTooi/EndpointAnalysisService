package com.ksptool.bio.biz.relay.repository;

import com.ksptool.bio.biz.relay.model.replayrequest.GetReplayRequestListDto;
import com.ksptool.bio.biz.relay.model.replayrequest.GetReplayRequestListVo;
import com.ksptool.bio.biz.relay.model.replayrequest.ReplayRequestPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplayRequestRepository extends CrudRepository<ReplayRequestPo, Long> {

    @Query("""
                    SELECT new com.ksptool.bio.biz.relay.model.replayrequest.GetReplayRequestListVo(
                        t.id,
                        t.requestId,
                        t.method,
                        t.url,
                        t.source,
                        t.status,
                        t.statusCode,
                        t.requestTime,
                        t.responseTime
                    )
                    FROM ReplayRequestPo t
                    WHERE
                    t.user.id = :uid AND
                    (:#{#dto.originRequestId} IS NULL OR t.originalRequest.requestId = :#{#dto.originRequestId}) 
                    AND (:#{#dto.requestId} IS NULL OR t.requestId = :#{#dto.requestId})
                    AND (:#{#dto.method} IS NULL OR t.method = :#{#dto.method})
                    AND (:#{#dto.url} IS NULL OR t.url LIKE CONCAT('%', :#{#dto.url}, '%'))
                    AND (:#{#dto.source} IS NULL OR t.source LIKE CONCAT('%', :#{#dto.source}, '%'))
                    AND (:#{#dto.status} IS NULL OR t.status = :#{#dto.status})
                    ORDER BY t.requestTime DESC
            """)
    Page<GetReplayRequestListVo> getReplayRequestList(@Param("dto") GetReplayRequestListDto dto, @Param("uid") Long uid, Pageable pageable);


}
