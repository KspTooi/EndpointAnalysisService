package com.ksptooi.biz.userrequest.repository;

import com.ksptooi.biz.userrequest.model.userrequestenv.UserRequestEnvPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestEnvRepository extends JpaRepository<UserRequestEnvPo, Long> {

    @Query("""
            SELECT u FROM UserRequestEnvPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.userId} IS NULL OR u.userId  = :#{#po.userId} )
            AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
            AND (:#{#po.remark} IS NULL OR u.remark  = :#{#po.remark} )
            AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<UserRequestEnvPo> getUserRequestEnvList(@Param("po") UserRequestEnvPo po, Pageable pageable);
}
