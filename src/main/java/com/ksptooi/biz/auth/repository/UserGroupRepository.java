package com.ksptooi.biz.auth.repository;

import com.ksptooi.biz.auth.model.UserGroupPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroupPo, UserGroupPo.Pk>, JpaSpecificationExecutor<UserGroupPo> {

    /**
     * 根据用户ID获取用户拥有的全部用户组ID
     *
     * @param userId 用户ID
     * @return 用户拥有的全部用户组ID
     */
    @Query("""
            SELECT ug.groupId FROM UserGroupPo ug
            WHERE ug.userId = :userId
            """)
    List<Long> getGroupIdsByGrantedUserId(@Param("userId") Long userId);

    /**
     * 清除用户拥有的全部用户组关联
     *
     * @param userId 用户ID
     * @return 清除的用户组关联数量
     */
    @Modifying
    @Query("""
            DELETE FROM UserGroupPo ug WHERE ug.userId = :userId
            """)
    void clearGroupGrantedByUserId(@Param("userId") Long userId);

}
