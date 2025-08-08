package com.ksptooi.biz.repository;

import com.ksptooi.biz.model.request.RequestPo;
import com.ksptooi.biz.model.request.GetRequestListDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface RequestRepository extends JpaRepository<RequestPo, Long> {

    @Query("""
            SELECT t FROM RequestPo t
            WHERE
            (:#{#dto.requestId} IS NULL OR t.requestId = :#{#dto.requestId})
            AND (:#{#dto.method} IS NULL OR t.method = :#{#dto.method})
            AND (:#{#dto.url} IS NULL OR t.url LIKE CONCAT('%', :#{#dto.url}, '%'))
            AND (:#{#dto.source} IS NULL OR t.source LIKE CONCAT('%', :#{#dto.source}, '%'))
            AND (:#{#dto.status} IS NULL OR t.status = :#{#dto.status})
            AND (
                    (:#{#dto.startTime} IS NULL AND :#{#dto.endTime} IS NULL) OR
                    (:#{#dto.startTime} <= t.requestTime OR :#{#dto.startTime} <= t.responseTime) OR
                    (:#{#dto.endTime} >= t.requestTime OR :#{#dto.endTime} >= t.responseTime)
               )
            ORDER BY t.requestTime DESC
    """)
    Page<RequestPo> getRequestList(@Param("dto") GetRequestListDto dto, Pageable pageable);

}
