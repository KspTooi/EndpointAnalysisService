package com.ksptooi.biz.userrequest.repository;


import com.ksptooi.biz.userrequest.model.userrequestgroup.UserRequestGroupPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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


    /**
     * 获取子级请求组 
     * @param userId 用户ID
     * @param parentId 父级ID 当为null时，获取所有Root级请求组
     * @return 子级请求组
     */
    @Query("""
    SELECT t FROM UserRequestGroupPo t
    WHERE
    t.user.id = :userId
    AND (:#{#parentId} IS NULL OR t.parent.id = :#{#parentId})
    AND (:#{#parentId} IS NOT NULL OR t.parent IS NULL)
    ORDER BY t.seq ASC,t.updateTime DESC
    """)
    List<UserRequestGroupPo> getChildGroupList(@Param("userId") Long userId, @Param("parentId") Long parentId);



    /**
     * 获取用户请求组及其请求
     * @param userId 用户ID
     * @return 用户请求组及其请求
     */
    @Query("""
        SELECT t FROM UserRequestGroupPo t
        LEFT JOIN FETCH t.requests
        WHERE t.user.id = :userId
        ORDER BY t.seq ASC,t.updateTime DESC
    """)
    List<UserRequestGroupPo> getUserRequestGroupWithRequests(@Param("userId") Long userId);

    @Query("""
        SELECT CASE WHEN COUNT(t) > 0 THEN :seq + 1 ELSE :seq END FROM UserRequestGroupPo t
        WHERE t.user.id = :userId AND t.seq = :seq
    """)
    Integer getNextSeq(@Param("userId") Long userId, @Param("seq") Integer seq);

}
