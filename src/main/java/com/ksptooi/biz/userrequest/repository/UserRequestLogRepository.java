package com.ksptooi.biz.userrequest.repository;

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
    SELECT u FROM UserRequestLogPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.requestId} IS NULL OR u.requestId  = :#{#po.requestId} )
    AND (:#{#po.method} IS NULL OR u.method  = :#{#po.method} )
    AND (:#{#po.url} IS NULL OR u.url  = :#{#po.url} )
    AND (:#{#po.source} IS NULL OR u.source  = :#{#po.source} )
    AND (:#{#po.requestHeaders} IS NULL OR u.requestHeaders  = :#{#po.requestHeaders} )
    AND (:#{#po.requestBodyLength} IS NULL OR u.requestBodyLength  = :#{#po.requestBodyLength} )
    AND (:#{#po.requestBodyType} IS NULL OR u.requestBodyType  = :#{#po.requestBodyType} )
    AND (:#{#po.requestBody} IS NULL OR u.requestBody  = :#{#po.requestBody} )
    AND (:#{#po.responseHeaders} IS NULL OR u.responseHeaders  = :#{#po.responseHeaders} )
    AND (:#{#po.responseBodyLength} IS NULL OR u.responseBodyLength  = :#{#po.responseBodyLength} )
    AND (:#{#po.responseBodyType} IS NULL OR u.responseBodyType  = :#{#po.responseBodyType} )
    AND (:#{#po.responseBody} IS NULL OR u.responseBody  = :#{#po.responseBody} )
    AND (:#{#po.statusCode} IS NULL OR u.statusCode  = :#{#po.statusCode} )
    AND (:#{#po.redirectUrl} IS NULL OR u.redirectUrl  = :#{#po.redirectUrl} )
    AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
    AND (:#{#po.responseTime} IS NULL OR u.responseTime  = :#{#po.responseTime} )
    ORDER BY u.requestTime DESC
    """)
    Page<UserRequestLogPo> getUserRequestLogList(@Param("po") UserRequestLogPo po, Pageable pageable);
}
