package com.ksptooi.biz.core.repository;

import com.ksptooi.biz.core.model.permission.PermissionPo;
import com.ksptooi.biz.core.model.user.GetUserListDto;
import com.ksptooi.biz.core.model.user.GetUserListVo;
import com.ksptooi.biz.core.model.user.UserPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserPo, Long> {


    @Query("""
            SELECT new com.ksptooi.biz.core.model.user.GetUserListVo(
                p.id,
                p.rootId,
                p.rootName,
                p.deptId,
                p.deptName,
                p.username,
                p.nickname,
                p.gender,
                p.phone,
                p.email,
                p.createTime,
                p.lastLoginTime,
                p.status,
                p.isSystem
            )
            FROM UserPo p
            LEFT JOIN OrgPo o ON p.deptId = o.id
            WHERE (:#{#dto.username} IS NULL OR p.username LIKE CONCAT('%', :#{#dto.username}, '%'))
              AND (:#{#dto.nickname} IS NULL OR p.nickname LIKE CONCAT('%', :#{#dto.nickname}, '%'))
              AND (:#{#dto.phone} IS NULL OR p.phone LIKE CONCAT('%', :#{#dto.phone}, '%'))
              AND (:#{#dto.status} IS NULL OR p.status = :#{#dto.status})
              AND (:#{#dto.orgId} IS NULL OR o.id = :#{#dto.orgId} OR o.orgPathIds LIKE CONCAT('%', :#{#dto.orgId}, '%'))
            """)
    Page<GetUserListVo> getUserList(@Param("dto") GetUserListDto dto, Pageable pageable);


    /**
     * 根据用户名统计用户数量(!!这会绕过软删除直接查询到被删除过的用户)
     *
     * @param username 用户名
     * @return 用户数量
     */
    @Query(
            value = """
                    SELECT COUNT(1)
                    FROM core_user
                    WHERE username = :username
                    """,
            nativeQuery = true
    )
    Integer countByUsername(@Param("username") String username);

    /**
     * 根据用户名统计用户数量 排除指定ID(!!这会绕过软删除直接查询到被删除过的用户)
     *
     * @param username 用户名
     * @param id       排除的ID
     * @return 用户数量
     */
    @Query(
            value = """
                    SELECT COUNT(1)
                    FROM core_user
                    WHERE username = :username
                      AND id != :id
                    """,
            nativeQuery = true
    )
    Integer countByUsernameExcludeId(@Param("username") String username, @Param("id") Long id);


    // 根据用户名查找用户
    UserPo findByUsername(String username);

    // 获取用户编辑视图，包含用户组信息
    @Query("""
            SELECT u
            FROM UserPo u
            LEFT JOIN FETCH u.groups
            WHERE u.id = :id
            """)
    UserPo getEditView(@Param("id") Long id);

    // 获取用户的所有权限（通过用户组）
    @Query("""
            SELECT DISTINCT p
            FROM UserPo u
            JOIN u.groups g
            JOIN g.permissions p
            WHERE u.id = :userId
            """)
    List<PermissionPo> findUserPermissions(@Param("userId") Long userId);


}