package com.ksptooi.biz.userrequest.repository;


import com.ksptooi.biz.userrequest.model.userrequestgroup.UserRequestGroupPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRequestGroupRepository extends JpaRepository<UserRequestGroupPo, Long>{

    @Query("""
    SELECT u FROM UserRequestGroupPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
    AND (:#{#po.description} IS NULL OR u.description  = :#{#po.description} )
    AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
    ORDER BY u.seq DESC
    """)
    Page<UserRequestGroupPo> getUserRequestGroupList(@Param("po") UserRequestGroupPo po, Pageable pageable);

    @Query("""
    SELECT t FROM UserRequestGroupPo t
    WHERE
    t.id = :id AND t.user.id = :userId
    """)
    UserRequestGroupPo getRequestGroupByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);



}
