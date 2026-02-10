package com.ksptooi.biz.qt.repository;

import com.ksptooi.biz.qt.model.qttask.QtTaskPo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QtTaskRepository extends JpaRepository<QtTaskPo, Long> {

    @Query("""
            SELECT u FROM QtTaskPo u
            WHERE
            (:#{#po.id} IS NULL OR u.id  = :#{#po.id} )
            AND (:#{#po.groupId} IS NULL OR u.groupId  = :#{#po.groupId} )
            AND (:#{#po.groupName} IS NULL OR u.groupName  LIKE CONCAT('%', :#{#po.groupName}, '%') )
            AND (:#{#po.name} IS NULL OR u.name  LIKE CONCAT('%', :#{#po.name}, '%') )
            AND (:#{#po.kind} IS NULL OR u.kind  = :#{#po.kind} )
            AND (:#{#po.cron} IS NULL OR u.cron  LIKE CONCAT('%', :#{#po.cron}, '%') )
            AND (:#{#po.target} IS NULL OR u.target  LIKE CONCAT('%', :#{#po.target}, '%') )
            AND (:#{#po.targetParam} IS NULL OR u.targetParam  LIKE CONCAT('%', :#{#po.targetParam}, '%') )
            AND (:#{#po.reqMethod} IS NULL OR u.reqMethod  LIKE CONCAT('%', :#{#po.reqMethod}, '%') )
            AND (:#{#po.concurrent} IS NULL OR u.concurrent  = :#{#po.concurrent} )
            AND (:#{#po.misfirePolicy} IS NULL OR u.misfirePolicy  = :#{#po.misfirePolicy} )
            AND (:#{#po.expireTime} IS NULL OR u.expireTime  = :#{#po.expireTime} )
            AND (:#{#po.status} IS NULL OR u.status  = :#{#po.status} )
            AND (:#{#po.createTime} IS NULL OR u.createTime  = :#{#po.createTime} )
            AND (:#{#po.creatorId} IS NULL OR u.creatorId  = :#{#po.creatorId} )
            AND (:#{#po.updateTime} IS NULL OR u.updateTime  = :#{#po.updateTime} )
            AND (:#{#po.updatorId} IS NULL OR u.updatorId  = :#{#po.updatorId} )
            AND (:#{#po.deleteTime} IS NULL OR u.deleteTime  = :#{#po.deleteTime} )
            ORDER BY u.updateTime DESC
            """)
    Page<QtTaskPo> getQtTaskList(@Param("po") QtTaskPo po, Pageable pageable);



    /**
     * 根据分组ID统计任务数量
     * @param groupId 分组ID
     * @return 任务数量
     */
    @Query("""
            SELECT COUNT(t) FROM QtTaskPo t WHERE t.groupId = :groupId
            """)
    Long countByGroupId(@Param("groupId") Long groupId);


    /**
     * 根据分组ID更新任务分组名
     * @param groupId 分组ID
     * @param groupName 分组名
     */
    @Modifying
    @Query("""
            UPDATE QtTaskPo t SET t.groupName = :groupName WHERE t.groupId = :groupId
            """)
    void updateGroupNameByGroupId(@Param("groupId") Long groupId, @Param("groupName") String groupName);
}
