package com.ksptooi.biz.requestdebug.repoistory;

import com.ksptooi.biz.requestdebug.model.userrequestlog.GetUserRequestLogListVo;
import com.ksptooi.biz.requestdebug.model.userrequestlog.UserRequestLogPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestLogRepository extends JpaRepository<UserRequestLogPo, Long> {

    @Query("""
            SELECT new com.ksptooi.biz.requestdebug.model.userrequestlog.GetUserRequestLogListVo
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

    @Query("""
            SELECT u FROM UserRequestLogPo u
            WHERE
            u.userRequest.id = :userRequestId
            ORDER BY u.requestTime DESC
            LIMIT 1
            """)
    UserRequestLogPo getLastUserRequestLog(@Param("userRequestId") Long userRequestId);

}
