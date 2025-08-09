package com.ksptooi.biz.user.repository;

import com.ksptooi.biz.user.model.permission.GetPermissionListDto;
import com.ksptooi.biz.user.model.permission.PermissionPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 权限数据访问接口
 */
@Repository
public interface PermissionRepository extends JpaRepository<PermissionPo, Long> {
    
    /**
     * 检查权限标识是否存在
     * @param code 权限标识
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 获取最大排序号
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
     * @param dto 包含查询条件的DTO对象
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