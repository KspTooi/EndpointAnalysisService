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
    (:#{#po.id} IS NULL OR u.id  LIKE CONCAT('%', :#{#po.id}, '%') )
    AND (:#{#po.username} IS NULL OR u.username  LIKE CONCAT('%', :#{#po.username}, '%') )
    AND (:#{#po.password} IS NULL OR u.password  LIKE CONCAT('%', :#{#po.password}, '%') )
    AND (:#{#po.nickname} IS NULL OR u.nickname  LIKE CONCAT('%', :#{#po.nickname}, '%') )
    AND (:#{#po.gender} IS NULL OR u.gender  LIKE CONCAT('%', :#{#po.gender}, '%') )
    AND (:#{#po.phone} IS NULL OR u.phone  LIKE CONCAT('%', :#{#po.phone}, '%') )
    AND (:#{#po.email} IS NULL OR u.email  LIKE CONCAT('%', :#{#po.email}, '%') )
    AND (:#{#po.loginCount} IS NULL OR u.loginCount  LIKE CONCAT('%', :#{#po.loginCount}, '%') )
    AND (:#{#po.status} IS NULL OR u.status  LIKE CONCAT('%', :#{#po.status}, '%') )
    AND (:#{#po.lastLoginTime} IS NULL OR u.lastLoginTime  LIKE CONCAT('%', :#{#po.lastLoginTime}, '%') )
    AND (:#{#po.rootId} IS NULL OR u.rootId  LIKE CONCAT('%', :#{#po.rootId}, '%') )
    AND (:#{#po.rootName} IS NULL OR u.rootName  LIKE CONCAT('%', :#{#po.rootName}, '%') )
    AND (:#{#po.deptId} IS NULL OR u.deptId  LIKE CONCAT('%', :#{#po.deptId}, '%') )
    AND (:#{#po.deptName} IS NULL OR u.deptName  LIKE CONCAT('%', :#{#po.deptName}, '%') )
    AND (:#{#po.activeCompanyId} IS NULL OR u.activeCompanyId  LIKE CONCAT('%', :#{#po.activeCompanyId}, '%') )
    AND (:#{#po.activeEnvId} IS NULL OR u.activeEnvId  LIKE CONCAT('%', :#{#po.activeEnvId}, '%') )
    AND (:#{#po.avatarAttachId} IS NULL OR u.avatarAttachId  LIKE CONCAT('%', :#{#po.avatarAttachId}, '%') )
    AND (:#{#po.isSystem} IS NULL OR u.isSystem  LIKE CONCAT('%', :#{#po.isSystem}, '%') )
    AND (:#{#po.dataVersion} IS NULL OR u.dataVersion  LIKE CONCAT('%', :#{#po.dataVersion}, '%') )
    AND (:#{#po.createTime} IS NULL OR u.createTime  LIKE CONCAT('%', :#{#po.createTime}, '%') )
    AND (:#{#po.creatorId} IS NULL OR u.creatorId  LIKE CONCAT('%', :#{#po.creatorId}, '%') )
    AND (:#{#po.updateTime} IS NULL OR u.updateTime  LIKE CONCAT('%', :#{#po.updateTime}, '%') )
    AND (:#{#po.updaterId} IS NULL OR u.updaterId  LIKE CONCAT('%', :#{#po.updaterId}, '%') )
    AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  LIKE CONCAT('%', :#{#po.deleteTime}, '%') )
    ORDER BY u.updateTime DESC
    """)
    Page<UserPo> getUserList(@Param("po") UserPo po, Pageable pageable);
}
