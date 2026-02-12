package com.ksptooi.biz.auth.repository;

import com.ksptooi.biz.auth.model.permission.PermissionPo;
import com.ksptooi.biz.auth.model.permission.dto.GetPermissionListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * 权限数据访问接口
 */
@Repository
public interface PermissionRepository extends JpaRepository<PermissionPo, Long> {

    /**
     * 从给定的权限列表中查找数据库中存在的权限
     *
     * @param codes 给定的权限代码列表
     * @return 数据库中存在的权限代码列表
     */
    @Query("""
            SELECT p.code
            FROM PermissionPo p
            WHERE p.code IN :codes
            """)
    Set<String> getExistingPermissionsByCode(@Param("codes") Set<String> codes);


    /**
     * 根据权限代码列表获取权限列表
     *
     * @param codes 权限代码列表
     * @return 权限列表
     */
    @Query("""
            SELECT DISTINCT p
            FROM PermissionPo p
            WHERE p.code IN :codes
            """)
    Set<PermissionPo> getPermissionsByCodes(@Param("codes") Set<String> codes);


    /**
     * 根据关键词和用户组ID获取权限列表
     *
     * @param keyword       关键词
     * @param groupId       用户组ID
     * @param hasPermission 是否拥有权限
     * @param pageable      分页信息
     * @return 权限列表
     * 如果hasPermission为1，则查询拥有该权限的权限列表
     * 如果hasPermission为0，则查询未拥有该权限的权限列表
     * 如果hasPermission为null，则查询全部权限列表
     */
    @Query("""
            SELECT DISTINCT p
            FROM PermissionPo p
            WHERE
            (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:groupId IS NULL OR :hasPermission IS NULL OR
                 ((:hasPermission = 1 AND EXISTS (SELECT 1 FROM GroupPermissionPo gp WHERE gp.groupId = :groupId))
                  OR (:hasPermission = 0 AND NOT EXISTS (SELECT 1 FROM GroupPermissionPo gp WHERE gp.groupId = :groupId))))
            ORDER BY p.sortOrder ASC
            """)
    Page<PermissionPo> getPermissionsByKeywordAndGroup(@Param("keyword") String keyword, @Param("groupId") Long groupId, @Param("hasPermission") Integer hasPermission, Pageable pageable);

    /**
     * 检查权限标识是否存在
     *
     * @param code 权限标识
     * @return 是否存在
     */
    boolean existsByCode(String code);


    /**
     * 根据关键词获取权限列表
     *
     * @param dto      查询条件
     * @param pageable 分页信息
     * @return 权限列表
     */
    @Query("""
            SELECT p
            FROM PermissionPo p
            WHERE (:#{#dto.code} IS NULL OR LOWER(p.code) LIKE LOWER(CONCAT('%', :#{#dto.code}, '%')))
            AND (:#{#dto.name} IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :#{#dto.name}, '%')))
            ORDER BY p.sortOrder ASC
            """)
    Page<PermissionPo> getPermissionList(
            @Param("dto") GetPermissionListDto dto,
            Pageable pageable);


    /**
     * 根据用户组ID获取权限列表
     *
     * @param groupId 用户组ID
     * @return 权限列表
     */
    @Query("""
            SELECT p FROM PermissionPo p
            LEFT JOIN GroupPermissionPo gp ON p.id = gp.permissionId
            WHERE gp.groupId = :groupId
            """)
    List<PermissionPo> getPermissionsByGroupId(@Param("groupId") Long groupId);

    /**
     * 获取全部权限ID列表
     *
     * @return 权限ID列表
     */
    @Query("""
            SELECT DISTINCT p.id FROM PermissionPo p
            """)
    List<Long> getAllPermissionIds();   


} 