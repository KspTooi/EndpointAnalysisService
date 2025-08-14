package com.ksptooi.biz.userrequest.repository;

import com.ksptooi.biz.userrequest.model.userrequestlog.GetUserRequestLogListVo;
import com.ksptooi.biz.userrequest.model.userrequestlog.UserRequestLogPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRequestLogRepository extends JpaRepository<UserRequestLogPo, Long>{

    @Query("""
    SELECT new com.ksptooi.biz.userrequest.model.userrequestlog.GetUserRequestLogListVo
    (
        u.id,
        u.requestId,
        u.method,
        u.url,
        u.source,
        u.status,
        u.statusCode,
        u.requestTime,
        u.responseTime
    ) FROM UserRequestLogPo u
    WHERE
    u.userRequest.id = :urId
    ORDER BY u.requestTime DESC
    """)
    Page<GetUserRequestLogListVo> getUserRequestLogList(@Param("urId") Long urId, Pageable pageable);
}
