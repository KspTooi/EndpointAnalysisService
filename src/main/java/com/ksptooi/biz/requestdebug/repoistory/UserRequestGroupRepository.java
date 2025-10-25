package com.ksptooi.biz.requestdebug.repoistory;


import com.ksptooi.biz.requestdebug.model.userrequestgroup.UserRequestGroupPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestGroupRepository extends JpaRepository<UserRequestGroupPo, Long> {


    @Query("""
            SELECT t FROM UserRequestGroupPo t
            WHERE
            t.id = :id AND t.user.id = :userId
            """)
    UserRequestGroupPo getRequestGroupByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);


}
