package com.ksptooi.biz.user.repository;

import com.ksptooi.biz.user.model.session.GetSessionListDto;
import com.ksptooi.biz.user.model.session.GetSessionListVo;
import com.ksptooi.biz.user.model.session.UserSessionPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionPo, Long> {


    @Query("""
          SELECT new com.ksptooi.biz.user.model.session.GetSessionListVo(
            us.id,
            u.username,
            us.createTime,
            us.expiresAt
          ) FROM UserSessionPo us
          LEFT JOIN UserPo u ON us.userId = u.id
          WHERE (:#{#dto.userName} IS NULL OR u.username LIKE %:#{#dto.userName}%)
          ORDER BY us.createTime DESC
          """)
    Page<GetSessionListVo> getSessionList(@Param("dto") GetSessionListDto dto, Pageable page);

    UserSessionPo findByToken(String token);

    UserSessionPo findByUserId(Long userId);

    void deleteByToken(String token);
    
    /**
     * 根据用户组ID查询该组下所有在线用户的会话信息
     * 在线用户的判断标准是会话未过期（expiresAt > 当前时间）
     * 
     * @param groupId 用户组ID
     * @return 在线用户的会话信息列表
     */
    @Query("""
          SELECT DISTINCT us FROM UserSessionPo us
          LEFT JOIN UserPo u ON us.userId = u.id
          LEFT JOIN u.groups ug
          WHERE (ug.id = :groupId)
          AND us.expiresAt > CURRENT_TIMESTAMP
          ORDER BY us.userId
          """)
    List<UserSessionPo> getUserSessionByGroupId(@Param("groupId") Long groupId);



}