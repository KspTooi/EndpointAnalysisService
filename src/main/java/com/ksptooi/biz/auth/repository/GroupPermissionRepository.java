package com.ksptooi.biz.auth.repository;

import com.ksptooi.biz.auth.model.GroupPermissionPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupPermissionRepository extends JpaRepository<GroupPermissionPo, GroupPermissionPo.Pk>, JpaSpecificationExecutor<GroupPermissionPo> {






    /**
     * 根据用户组ID获取拥有的全部权限ID
     *
     * @param groupId 用户组ID
     * @return 权限ID列表
     */
    @Query("""
            SELECT gp.permissionId FROM GroupPermissionPo gp
            WHERE gp.groupId = :groupId
            """)
    List<Long> getPermissionIdsByGroupId(@Param("groupId") Long groupId);
    

    /**
     * 根据用户组ID获取拥有的全部权限
     *
     * @param groupId 用户组ID
     * @return 权限列表
     */
    @Query("""
            SELECT gp FROM GroupPermissionPo gp
            WHERE gp.groupId = :groupId
            """)
    List<GroupPermissionPo> getGroupPermissionsByGroupId(@Param("groupId") Long groupId);

    /**
     * 清除用户组拥有的全部权限关联
     *
     * @param groupId 用户组ID
     */
    @Modifying
    @Query("""
            DELETE FROM GroupPermissionPo gp WHERE gp.groupId = :groupId
            """)
    void clearPermissionByGroupId(@Param("groupId") Long groupId);

    /**
     * 根据权限码ID列表清除全部权限关联
     * 
     * @param permissionIds 权限码ID列表 列表不能为空
     */
    @Modifying
    @Query("""
            DELETE FROM GroupPermissionPo gp WHERE gp.permissionId IN :permissionIds
            """)
    void clearGpByPermissionIds(@Param("permissionIds") List<Long> permissionIds);


    /**
     * 根据用户组ID列表清除权限关联
     *
     * @param groupIds 用户组ID列表 列表不能为空
     */
    @Modifying
    @Query("""
            DELETE FROM GroupPermissionPo gp WHERE gp.groupId IN :groupIds
            """)
    void clearPermissionByGroupIds(@Param("groupIds") List<Long> groupIds);

    
    /**
     * 根据权限ID统计有多少用户组在使用该权限
     *
     * @param permissionId 权限ID
     * @return 用户组数量
     */
    @Query("""
            SELECT COUNT(gp) FROM GroupPermissionPo gp WHERE gp.permissionId = :permissionId
            """)
    Long countGroupPermissionByPermissionId(@Param("permissionId") Long permissionId);

}
