package com.ksptooi.biz.relay.repository;

import com.ksptooi.biz.relay.model.replayrequest.GetOriginRequestVo;
import com.ksptooi.biz.relay.model.request.GetRequestListDto;
import com.ksptooi.biz.relay.model.request.GetRequestListVo;
import com.ksptooi.biz.relay.model.request.RequestPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RequestRepository extends JpaRepository<RequestPo, Long> {

    @Query("""
                    SELECT new com.ksptooi.biz.relay.model.request.GetRequestListVo(
                        t.id,
                        t.requestId,
                        t.method,
                        t.url,
                        t.source,
                        t.status,
                        t.statusCode,
                        SIZE(t.replayRequests) ,
                        t.requestTime,
                        t.responseTime
                    )
                    FROM RequestPo t
                    WHERE
                    (:#{#dto.requestId} IS NULL OR t.requestId = :#{#dto.requestId})
                    AND (:#{#dto.relayServerId} IS NULL OR t.relayServer.id = :#{#dto.relayServerId})
                    AND (:#{#dto.method} IS NULL OR t.method = :#{#dto.method})
                    AND (:#{#dto.url} IS NULL OR t.url LIKE CONCAT('%', :#{#dto.url}, '%'))
                    AND (:#{#dto.source} IS NULL OR t.source LIKE CONCAT('%', :#{#dto.source}, '%'))
                    AND (:#{#dto.status} IS NULL OR t.status = :#{#dto.status})
                    AND (:#{#dto.replay} IS NULL OR :#{#dto.replay} = 0 OR (SIZE(t.replayRequests) > 0))
                    AND (
                            (:#{#dto.startTime} IS NULL AND :#{#dto.endTime} IS NULL) OR
                            (
                                (:#{#dto.startTime} IS NULL OR t.requestTime >= :#{#dto.startTime}) AND
                                (:#{#dto.endTime} IS NULL OR t.requestTime <= :#{#dto.endTime})
                            )
                       )
                    ORDER BY t.requestTime DESC
            """)
    Page<GetRequestListVo> getRequestList(@Param("dto") GetRequestListDto dto, Pageable pageable);

    /**
     * 根据请求ID获取原始请求
     *
     * @param requestId 请求ID
     * @return 原始请求
     */
    @Query("""
                SELECT new com.ksptooi.biz.relay.model.replayrequest.GetOriginRequestVo(
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
                FROM RequestPo t
                WHERE
                t.requestId = :requestId
            """)
    GetOriginRequestVo getRequestByRequestId(@Param("requestId") String requestId);


    @Query("""
                SELECT t
                FROM RequestPo t
                WHERE t.requestId = :requestId
            """)
    RequestPo getByRequestId(@Param("requestId") String requestId);


}
