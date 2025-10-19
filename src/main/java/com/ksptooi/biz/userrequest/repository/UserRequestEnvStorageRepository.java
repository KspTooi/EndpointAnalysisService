package com.ksptooi.biz.userrequest.repository;

import com.ksptooi.biz.userrequest.model.userrequestenvstorage.UserRequestEnvStoragePo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestEnvStorageRepository extends JpaRepository<UserRequestEnvStoragePo, Long> {

    @Query("""
            SELECT u FROM UserRequestEnvStoragePo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.envId} IS NULL OR u.envId  = :#{#po.envId} )
            AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
            AND (:#{#po.initValue} IS NULL OR u.initValue  = :#{#po.initValue} )
            AND (:#{#po.value} IS NULL OR u.value  = :#{#po.value} )
            AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<UserRequestEnvStoragePo> getUserRequestEnvStorageList(@Param("po") UserRequestEnvStoragePo po, Pageable pageable);
}
