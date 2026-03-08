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
     (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
     AND (:#{#po.username} IS NULL OR u.username  LIKE CONCAT('%', :#{#po.username}, '%') )
     AND (:#{#po.nickname} IS NULL OR u.nickname  LIKE CONCAT('%', :#{#po.nickname}, '%') )
     AND (:#{#po.gender} IS NULL OR u.gender  = :#{#po.gender} )
     AND (:#{#po.phone} IS NULL OR u.phone  LIKE CONCAT('%', :#{#po.phone}, '%') )
     AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
    ORDER BY u.updateTime DESC
    """)
    Page<UserPo> getUserList(@Param("po") UserPo po, Pageable pageable);
}
