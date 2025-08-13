package com.ksptooi.biz.userrequest.repository;


import com.ksptooi.biz.userrequest.model.userrequest.UserRequestPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRequestRepository extends JpaRepository<UserRequestPo, Long>{

    @Query("""
    SELECT u FROM UserRequestPo u
    WHERE
    (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
    AND (:#{#po.name} IS NULL OR u.name  = :#{#po.name} )
    AND (:#{#po.seq} IS NULL OR u.seq  = :#{#po.seq} )
    ORDER BY u.seq ASC
    """)
    Page<UserRequestPo> getUserRequestList(@Param("po") UserRequestPo po, Pageable pageable);


    @Query("""
        SELECT u FROM UserRequestPo u
        LEFT JOIN FETCH u.group g
        ORDER BY u.seq,g.seq ASC
    """)
    Page<UserRequestPo> getUserRequestWithGroup(@Param("keyword") String keyword, Pageable pageable);
}
