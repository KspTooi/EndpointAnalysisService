package com.ksptooi.biz.auth.repository;

import com.ksptooi.biz.auth.model.permission.GetPermissionListDto;
import com.ksptooi.biz.auth.model.permission.PermissionPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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


    @Query("""
            SELECT DISTINCT p
            FROM PermissionPo p
            WHERE p.code IN :codes
            """)
    Set<PermissionPo> getPermissionsByCodes(@Param("codes") Set<String> codes);


    @Query("""
            SELECT DISTINCT p
            FROM PermissionPo p
            WHERE
            (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
            AND (:groupId IS NULL OR :hasPermission IS NULL OR 
                 ((:hasPermission = 1 AND EXISTS (SELECT 1 FROM p.groups g WHERE g.id = :groupId))
                  OR (:hasPermission = 0 AND NOT EXISTS (SELECT 1 FROM p.groups g WHERE g.id = :groupId))))
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
     * 获取最大排序号
     *
     * @return 最大排序号，如果没有记录则返回0
     */
    @Query("""
            SELECT COALESCE(MAX(p.sortOrder), 0)
            FROM PermissionPo p
            """)
    Integer findMaxSortOrder();

    /**
     * 使用JPQL查询权限列表，根据DTO中的条件进行筛选
     *
     * @param dto      包含查询条件的DTO对象
     * @param pageable 分页信息
     * @return 权限PO对象分页
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
} 