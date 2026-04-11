package com.ksptool.bio.biz.user.repository;

import com.ksptool.bio.biz.user.model.UserPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRepository extends JpaRepository<UserPo, Long>{

    @Query("""
    SELECT u FROM UserPo u
    WHERE
    AND (:#{#po.username} IS NULL OR u.username LIKE CONCAT('%', :#{#po.username}, '%'))
    AND (:#{#po.password} IS NULL OR u.password LIKE CONCAT('%', :#{#po.password}, '%'))
    AND (:#{#po.nickname} IS NULL OR u.nickname LIKE CONCAT('%', :#{#po.nickname}, '%'))
    AND (:#{#po.gender} IS NULL OR u.gender = :#{#po.gender} )
    AND (:#{#po.phone} IS NULL OR u.phone LIKE CONCAT('%', :#{#po.phone}, '%'))
    AND (:#{#po.email} IS NULL OR u.email LIKE CONCAT('%', :#{#po.email}, '%'))
    AND (:#{#po.loginCount} IS NULL OR u.loginCount LIKE CONCAT('%', :#{#po.loginCount}, '%'))
    AND (:#{#po.status} IS NULL OR u.status = :#{#po.status} )
    AND (:#{#po.lastLoginTime} IS NULL OR u.lastLoginTime = :#{#po.lastLoginTime} )
    AND (:#{#po.rootId} IS NULL OR u.rootId = :#{#po.rootId} )
    AND (:#{#po.rootName} IS NULL OR u.rootName LIKE CONCAT('%', :#{#po.rootName}, '%'))
    AND (:#{#po.deptId} IS NULL OR u.deptId = :#{#po.deptId} )
    AND (:#{#po.deptName} IS NULL OR u.deptName LIKE CONCAT('%', :#{#po.deptName}, '%'))
    AND (:#{#po.activeCompanyId} IS NULL OR u.activeCompanyId = :#{#po.activeCompanyId} )
    AND (:#{#po.activeEnvId} IS NULL OR u.activeEnvId = :#{#po.activeEnvId} )
    AND (:#{#po.avatarAttachId} IS NULL OR u.avatarAttachId = :#{#po.avatarAttachId} )
    AND (:#{#po.isSystem} IS NULL OR u.isSystem = :#{#po.isSystem} )
    AND (:#{#po.dataVersion} IS NULL OR u.dataVersion = :#{#po.dataVersion} )
    AND (:#{#po.createTime} IS NULL OR u.createTime = :#{#po.createTime} )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId = :#{#po.creatorId} )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime = :#{#po.updateTime} )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId = :#{#po.updaterId} )
    AND (:#{#po.deleteTime} IS NULL OR u.deleteTime = :#{#po.deleteTime} )
    ORDER BY u.updateTime DESC
    """)
    Page<UserPo> getUserList(@Param("po") UserPo po, Pageable pageable);
}
