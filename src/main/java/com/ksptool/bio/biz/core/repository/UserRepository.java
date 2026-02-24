package com.ksptool.bio.biz.core.repository;

import com.ksptool.bio.biz.auth.model.permission.PermissionPo;
import com.ksptool.bio.biz.core.model.user.UserPo;
import com.ksptool.bio.biz.core.model.user.dto.GetUserListDto;
import com.ksptool.bio.biz.core.model.user.vo.GetUserListVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<UserPo, Long> {

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    @Query("""
            SELECT p FROM UserPo p
            WHERE p.username = :username
            """)
    UserPo getUserByUsername(@Param("username") String username);


    @Query("""
            SELECT new com.ksptool.bio.biz.core.model.user.vo.GetUserListVo(
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
              AND (:#{#dto.orgId} IS NULL OR o.id = :#{#dto.orgId} OR o.orgPathIds LIKE CONCAT('%', :#{#dto.orgId}, '%')) OR o.rootId = :#{#dto.orgId}
            ORDER BY p.createTime DESC
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


    // 获取用户的所有权限（通过用户组）
    @Query("""
            SELECT DISTINCT p
            FROM UserGroupPo ug
            JOIN GroupPermissionPo gp ON ug.groupId = gp.groupId
            JOIN PermissionPo p ON gp.permissionId = p.id
            WHERE ug.userId = :userId
            """)
    List<PermissionPo> getUserPermissions(@Param("userId") Long userId);


    /**
     * 获取用户权限代码列表
     *
     * @param userId 用户ID
     * @return 权限代码列表
     */
    @Query("""
            SELECT DISTINCT p.code
            FROM UserGroupPo ug
            JOIN GroupPermissionPo gp ON ug.groupId = gp.groupId
            JOIN PermissionPo p ON gp.permissionId = p.id
            WHERE ug.userId = :userId
            """)
    Set<String> getUserPermissionCodes(@Param("userId") Long userId);


    /**
     * 根据用户名列表查找用户
     *
     * @param usernames 用户名列表
     * @return 用户名列表
     */
    @Query("""
            SELECT DISTINCT p.username
            FROM UserPo p
            WHERE p.username IN :usernames
            """)
    Set<String> getUsernameSetByUsernames(@Param("usernames") List<String> usernames);


    /**
     * 获取用户ID列表
     *
     * @param pageable 分页信息
     * @return 用户ID列表
     */
    @Query("""
            SELECT p.id
            FROM UserPo p
            """)
    List<Long> getUserIdsList(Pageable pageable);

    /**
     * 统计用户数量
     */
    @Query("""
            SELECT COUNT(p.id) FROM UserPo p
            """)
    Long countUser();

    /**
     * 根据部门ID列表获取用户列表
     *
     * @param deptIds 部门ID列表
     * @return 用户列表
     */
    @Query("""
            SELECT p FROM UserPo p
            WHERE p.deptId IN :deptIds
            """)
    List<UserPo> getUserListByDeptIds(@Param("deptIds") List<Long> deptIds);


    /**
     * 统计系统内置用户数量
     *
     * @return 系统内置用户数量
     */
    @Query("""
            SELECT COUNT(p.id) FROM UserPo p WHERE p.isSystem = 1
            """)
    Integer countBySystemUser();

    /**
     * 根据用户ID获取数据版本
     *
     * @param userId 用户ID
     * @return 数据版本
     */
    @Query("""
            SELECT p.dataVersion FROM UserPo p
            WHERE p.id = :userId
            """)
    Long getDvByUserId(@Param("userId") Long userId);

}