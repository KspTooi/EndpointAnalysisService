package com.ksptooi.biz.auth.repository;

import com.ksptooi.biz.auth.model.GroupDeptPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupDeptRepository extends JpaRepository<GroupDeptPo, GroupDeptPo.Pk> {

    @Query("""
            SELECT u FROM GroupDeptPo u
            WHERE
            (:#{#po.groupId} IS NULL OR u.groupId = :#{#po.groupId})
            AND (:#{#po.deptId} IS NULL OR u.deptId = :#{#po.deptId})
            AND (:#{#po.createTime} IS NULL OR u.createTime = :#{#po.createTime})
            ORDER BY u.createTime DESC
            """)
    Page<GroupDeptPo> getAuthGroupDeptList(@Param("po") GroupDeptPo po, Pageable pageable);


    /**
     * 根据用户组ID清除部门关系
     * @param groupId 用户组ID
     */
    @Modifying
    @Query("""
            DELETE FROM GroupDeptPo u WHERE u.groupId = :groupId
            """)
    Integer clearGroupDeptByGroupId(@Param("groupId") Long groupId);

    /**
     * 根据用户组ID获取部门ID列表
     * @param groupId 用户组ID
     * @return 部门ID列表
     */
    @Query("""
            SELECT u.deptId FROM GroupDeptPo u WHERE u.groupId = :groupId
            """)
    List<Long> getDeptIdsByGroupId(@Param("groupId") Long groupId);

}
