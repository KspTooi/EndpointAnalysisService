package com.ksptooi.biz.auth.repository;


import com.ksptooi.biz.auth.model.group.GroupPo;
import com.ksptooi.biz.auth.model.group.dto.GetGroupListDto;
import com.ksptooi.biz.auth.model.group.vo.GetGroupListVo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupRepository extends JpaRepository<GroupPo, Long>, JpaSpecificationExecutor<GroupPo> {


    /**
     * 根据用户ID获取用户拥有的全部用户组
     *
     * @param userId 用户ID
     * @return 用户拥有的全部用户组
     */
    @Query("""
            SELECT g FROM GroupPo g
            LEFT JOIN UserGroupPo ug ON g.id = ug.groupId
            WHERE ug.userId = :userId
            """)
    List<GroupPo> getGroupsByUserId(@Param("userId") Long userId);

    /**
     * 检查用户组标识是否存在
     */
    boolean existsByCode(String code);

    /**
     * 根据ID列表获取用户组及其关联的用户和权限
     *
     * @param ids 用户组ID列表
     * @return 用户组及其关联的用户和权限
     */
    @Query("""
            SELECT g FROM GroupPo g
            WHERE g.id IN :ids
            """)
    List<GroupPo> getGroupsByIds(@Param("ids") List<Long> ids);

    /**
     * 获取最大排序号
     *
     * @return 最大排序号，如果没有记录则返回0
     */
    @Query("""
            SELECT COALESCE(MAX(g.seq), 0)
            FROM GroupPo g
            """)
    Integer findMaxSortOrder();

    @Query("""
            SELECT new com.ksptooi.biz.auth.model.group.vo.GetGroupListVo(
                g.id,
                g.code,
                g.name,
                CAST((SELECT COUNT(ug) FROM UserGroupPo ug WHERE ug.groupId = g.id) AS integer),
                CAST((SELECT COUNT(gp) FROM GroupPermissionPo gp WHERE gp.groupId = g.id) AS integer),
                g.isSystem,
                g.status,
                g.createTime
            )
            FROM GroupPo g
            WHERE (:#{#dto.keyword} IS NULL OR g.code LIKE %:#{#dto.keyword}%
                OR g.name LIKE %:#{#dto.keyword}%
                OR g.remark LIKE %:#{#dto.keyword}%)
            AND (:#{#dto.status} IS NULL OR g.status = :#{#dto.status})
            ORDER BY g.seq ASC, g.id DESC
            """)
    Page<GetGroupListVo> getGroupList(@Param("dto") GetGroupListDto dto, Pageable pageable);


    /**
     * 根据组码获取用户组
     *
     * @param code 组码
     * @return 用户组
     */
    @Query("""
            SELECT g FROM GroupPo g WHERE g.code = :code
            """)
    GroupPo getGroupByCode(@Param("code") String code);

    /**
     * 统计系统内置组数量
     *
     * @return 系统内置组数量
     */
    @Query("""
            SELECT COUNT(g) FROM GroupPo g WHERE g.isSystem = 1
            """)
    Integer countBySystemGroup();

    /**
     * 根据ID列表获取用户组
     *
     * @param ids    用户组ID列表
     * @param status 用户组状态 0:禁用 1:启用
     * @return 用户组
     */
    @Query("""
            SELECT g.id FROM GroupPo g
            WHERE g.id IN :ids AND g.status = :status
            """)
    List<Long> getUserGroupByIds(@Param("ids") List<Long> ids, Integer status);
}