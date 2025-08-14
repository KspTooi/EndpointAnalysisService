package com.ksptooi.biz.userrequest.repository;


import com.ksptooi.biz.userrequest.model.userrequest.UserRequestPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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

    @Query("""
        SELECT u FROM UserRequestPo u
        WHERE u.user.id = :userId AND u.group IS NULL
        ORDER BY u.seq ASC,u.updateTime DESC
    """)
    List<UserRequestPo> getNotInGroupUserRequestList(@Param("userId") Long userId);

    /**
     * 根据ID和用户ID获取用户请求
     * @param id 用户请求ID
     * @param userId 用户ID
     * @return 用户请求
     */
    @Query("""
        SELECT u FROM UserRequestPo u
        WHERE u.id = :id AND u.user.id = :userId
    """)
    UserRequestPo getByIdAndUserId(@Param("id") Long id,@Param("userId") Long userId);


    @Query("""
        SELECT (COALESCE(MAX(t.seq), 0) + 1) FROM UserRequestPo t
        WHERE t.user.id = :userId
    """)
    Integer getNextSeq(@Param("userId") Long userId);
}
