package com.ksptooi.biz.userrequest.repository;

import com.ksptooi.biz.userrequest.model.userrequest.UserRequestPo;
import com.ksptooi.biz.userrequest.model.userrequesttree.UserRequestTreePo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRequestTreeRepository extends JpaRepository<UserRequestTreePo, Long> {


    @Query("""
            SELECT COALESCE(MAX(t.seq), 1) FROM UserRequestTreePo t
            WHERE (:parentId IS NULL OR t.parent.id = :parentId) AND
                  (:parentId IS NOT NULL OR t.parent IS NULL)
            """)
    Integer getMaxSeqInParent(@Param("parentId") Long parentId);




}
