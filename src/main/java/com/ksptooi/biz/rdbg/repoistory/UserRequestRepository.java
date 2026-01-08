package com.ksptooi.biz.rdbg.repoistory;


import com.ksptooi.biz.rdbg.model.userrequest.UserRequestPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestRepository extends JpaRepository<UserRequestPo, Long> {


    /**
     * 根据ID和用户ID获取用户请求
     *
     * @param id     用户请求ID
     * @param userId 用户ID
     * @return 用户请求
     */
    @Query("""
                SELECT u FROM UserRequestPo u
                WHERE u.id = :id AND u.user.id = :userId
            """)
    UserRequestPo getByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

}
