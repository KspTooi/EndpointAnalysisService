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
            u.user.id = :userId
            AND (:#{#po.name} IS NULL OR u.name LIKE CONCAT('%', :#{#po.name}, '%'))
            ORDER BY u.createTime DESC
            """)
    Page<UserRequestEnvPo> getUserRequestEnvList(@Param("po") UserRequestEnvPo po, @Param("userId") Long userId, Pageable pageable);

    @Query("""
            SELECT u FROM UserRequestEnvPo u
            WHERE
            u.id = :id AND u.user.id = :userId
            """)
    UserRequestEnvPo getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
