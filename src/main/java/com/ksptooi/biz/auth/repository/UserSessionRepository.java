package com.ksptooi.biz.auth.repository;

import com.ksptooi.biz.auth.model.session.dto.GetSessionListDto;
import com.ksptooi.biz.auth.model.session.vo.GetSessionListVo;
import com.ksptooi.biz.auth.model.session.UserSessionPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface UserSessionRepository extends JpaRepository<UserSessionPo, Long> {

    /**
     * 获取会话列表
     *
     * @param dto  查询条件
     * @param page 分页信息
     * @return 会话列表
     */
    @Query("""
            SELECT new com.ksptooi.biz.core.model.session.GetSessionListVo(
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


    /**
     * 根据SessionId获取会话
     *
     * @param sessionId 会话SessionId
     * @return 会话
     */
    @Query("""
                SELECT us FROM UserSessionPo us
                WHERE us.sessionId = :sessionId
            """)
    UserSessionPo getSessionBySessionId(@Param("sessionId") String sessionId);


    /**
     * 根据用户ID获取会话
     *
     * @param userId 用户ID
     * @return 会话
     */
    @Query("""
                SELECT us FROM UserSessionPo us
                WHERE us.userId = :userId
            """)
    UserSessionPo getSessionByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("""
            DELETE FROM UserSessionPo us
            WHERE us.userId IN :userIds
            """)
    int removeUserSessionByUserIds(@Param("userIds") List<Long> userIds);

    /**
     * 根据SessionId删除会话
     */
    @Modifying
    @Query("""
            DELETE FROM UserSessionPo us
            WHERE us.sessionId = :sessionId
            """)
    void removeUserSessionBySessionId(@Param("sessionId") String sessionId);

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