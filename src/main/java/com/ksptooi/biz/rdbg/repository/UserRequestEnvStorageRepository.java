package com.ksptooi.biz.rdbg.repository;

import com.ksptooi.biz.rdbg.model.userrequestenvstorage.UserRequestEnvStoragePo;
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
            u.env.id = :requestEnvId
            AND (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.initValue} IS NULL OR u.initValue LIKE CONCAT('%', :#{#po.initValue}, '%') )
            AND (:#{#po.value} IS NULL OR u.value LIKE CONCAT('%', :#{#po.value}, '%') )
            AND (:#{#po.status} IS NULL OR u.status = :#{#po.status} )
            ORDER BY u.updateTime DESC
            """)
    Page<UserRequestEnvStoragePo> getUserRequestEnvStorageList(@Param("po") UserRequestEnvStoragePo po, @Param("requestEnvId") Long requestEnvId, Pageable pageable);
}
