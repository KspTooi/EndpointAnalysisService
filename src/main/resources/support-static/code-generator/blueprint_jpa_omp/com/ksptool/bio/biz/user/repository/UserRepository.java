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
    ORDER BY u.createTime DESC
    """)
    Page<UserPo> getUserList(@Param("po") UserPo po, Pageable pageable);
}
