package com.ksptool.bio.biz.todo.repository;

import com.ksptool.bio.biz.todo.model.TodoPo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface TodoRepository extends JpaRepository<TodoPo, Long>{

    @Query("""
    SELECT u FROM TodoPo u
    WHERE
    (:#{#po.summary} IS NULL OR u.summary LIKE CONCAT('%', :#{#po.summary}, '%'))
    AND (:#{#po.memberType} IS NULL OR u.memberType = :#{#po.memberType} )
    AND (:#{#po.memberId} IS NULL OR u.memberId = :#{#po.memberId} )
    AND (:#{#po.initiatorId} IS NULL OR u.initiatorId = :#{#po.initiatorId} )
    AND (:#{#po.createTime} IS NULL OR u.createTime = :#{#po.createTime} )
    ORDER BY u.createTime DESC
    """)
    Page<TodoPo> getTodoList(@Param("po") TodoPo po, Pageable pageable);
}
